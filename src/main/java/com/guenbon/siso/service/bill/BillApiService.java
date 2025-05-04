package com.guenbon.siso.service.bill;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.CongressApiClient;
import com.guenbon.siso.dto.bill.response.BillBatchResultDTO;
import com.guenbon.siso.dto.bill.response.SyncBillResultDTO;
import com.guenbon.siso.dto.bill.response.SyncBillSummaryResultDTO;
import com.guenbon.siso.entity.bill.Bill;
import com.guenbon.siso.entity.bill.BillSummary;
import com.guenbon.siso.factory.BillFactory;
import com.guenbon.siso.service.billsummary.BillSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.guenbon.siso.support.constants.ApiConstants.BILL_API_PATH;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillApiService {

    @Value("${bill.api.maxpage}")
    private int billApiMaxPage;
    @Value("${bil.batch.insert.size}")
    private int billBatchInsertSize;
    private final CongressApiClient congressApiClient;
    private final BillService billService;
    private final BillSummaryService billSummaryService;

    @Scheduled(cron = "0 0 3 ? * MON")
    public BillBatchResultDTO fetchAndSyncBillAndBillSummary() {
        SyncBillResultDTO syncBillResultDTO = fetchAndSyncBill();
        SyncBillSummaryResultDTO syncBillSummaryResultDTO = syncBillSummaries(syncBillResultDTO).block();
        return BillBatchResultDTO.of(syncBillResultDTO, syncBillSummaryResultDTO);
    }

    /**
     * 국회 api 에서 22대 국회 발의안 전체 목록을 가져와 db와 동기화한다
     *
     * @return
     */
    private SyncBillResultDTO fetchAndSyncBill() {
        List<Bill> apiBillList = new ArrayList<>();
        Map<String, List<String>> billProposerMap = new HashMap<>();

        int page = 1;

        while (page <= billApiMaxPage) { // 혹시 모를 무한루프 방지
            final JsonNode jsonNode = congressApiClient.getBillResponse(page++);

            if (congressApiClient.isApiResponseError(jsonNode)) {
                if (congressApiClient.isLastPage(jsonNode)) break;
                congressApiClient.handleApiError(jsonNode);
            }

            final JsonNode billRowNodeList = congressApiClient.getContent(jsonNode, BILL_API_PATH);

            extractBillsFromApiResponse(billRowNodeList, billProposerMap, apiBillList);
        }

        return billService.syncBill(apiBillList, billProposerMap);
    }

    private void extractBillsFromApiResponse(JsonNode billRowNodeList, Map<String, List<String>> billProposerMap, List<Bill> apiBillList) {
        for (JsonNode billNode : billRowNodeList) {
            final Bill bill = BillFactory.from(billNode);
            final List<String> proposerNameList = getProposerNameList(billNode);
            billProposerMap.put(bill.getBillId(), proposerNameList);
            apiBillList.add(bill);
        }
    }

    /**
     * 발의자 목록 파싱 후 반환
     * todo : 동명이인 있을 시 문제되므로 수정필요
     *
     * @param billNode
     * @return
     */
    private List<String> getProposerNameList(JsonNode billNode) {
        final List<String> proposerList = new ArrayList<>();

        final String publProposer = billNode.path("PUBL_PROPOSER").asText("").trim(); // 공동 발의자
        final String rstProposer = billNode.path("RST_PROPOSER").asText("").trim();   // 대표 발의자

        // 대표 발의자 분리 후 추가
        if (!rstProposer.isBlank()) {
            String[] rstProposerList = rstProposer.split(",");
            for (String proposer : rstProposerList) {
                if (!proposer.isBlank()) {
                    proposerList.add(proposer.trim());
                }
            }
        }

        // 공동 발의자 분리 후 추가
        if (!publProposer.isBlank()) {
            String[] publProposerList = publProposer.split(",");
            for (String proposer : publProposerList) {
                if (!proposer.isBlank()) {
                    proposerList.add(proposer.trim());
                }
            }
        }

        return proposerList;
    }

    private Mono<SyncBillSummaryResultDTO> syncBillSummaries(SyncBillResultDTO syncBillResultDTO) {
        AtomicInteger count = new AtomicInteger(0);

        return Flux.fromIterable(syncBillResultDTO.getInsertList())
                .flatMap(bill -> {
                    int current = count.incrementAndGet();

                    try {
                        String scrapResult = billService.scrapData(bill.getDetailLink());

                        return congressApiClient.getBillSummaryResponse(scrapResult)
                                .map(dto -> {
                                    log.info("[{}] bill {} 요약 api 성공", current, bill.getId());
                                    return BillSummary.of(dto, bill);
                                })
                                .onErrorResume(e -> {
                                    log.error("[{}] bill {} 요약 api 실패: {}", current, bill.getId(), e.getMessage());
                                    return Mono.empty();
                                });

                    } catch (Exception e) {
                        log.error("[{}] bill {} 처리 중 예외 발생: {}", current, bill.getId(), e.getMessage());
                        return Mono.empty();
                    }
                })
                .buffer(billBatchInsertSize) // ✅ 100개 단위로 묶음
                .flatMap(batch -> {
                    try {
                        billSummaryService.saveAll(batch); // 저장
                        return Flux.fromIterable(batch);   // 다시 풀어서 내려보냄
                    } catch (Exception e) {
                        log.error("batch 저장 실패: {}", e.getMessage());
                        return Flux.empty(); // 실패 시 그냥 버림
                    }
                })
                .collectList() // 모든 성공 저장된 BillSummary 모음
                .map(SyncBillSummaryResultDTO::of);
    }
}
