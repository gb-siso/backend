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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.guenbon.siso.support.constants.ApiConstants.BILL_API_PATH;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillApiService {

    private static final Logger schedulerLogger = LoggerFactory.getLogger("SCHEDULER_LOGGER");

    @Value("${bill.api.maxpage}")
    private int billApiMaxPage;
    @Value("${bil.batch.insert.size}")
    private int billBatchInsertSize;
    private final CongressApiClient congressApiClient;
    private final BillService billService;
    private final BillSummaryService billSummaryService;

    @Scheduled(cron = "0 0 3 ? * MON")
    public BillBatchResultDTO fetchAndSyncBillAndBillSummary() {

        schedulerLogger.info("[Bill 동기화] 시작 : {}", LocalDate.now());
        SyncBillResultDTO syncBillResultDTO = fetchAndSyncBill();
        schedulerLogger.info("[Bill 동기화] 끝 : {}", LocalDate.now());

        schedulerLogger.info("[BillSummary 동기화] 시작 : {}", LocalDate.now());
        SyncBillSummaryResultDTO syncBillSummaryResultDTO = syncBillSummaries().block();
        schedulerLogger.info("[BillSummary 동기화] 끝 : {}", LocalDate.now());

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

            // 2025-01-01 이후 데이터만 관리 (나머지 폐기)
            LocalDate proposeDt = bill.getProposeDt();
            if (proposeDt != null && proposeDt.isBefore(LocalDate.of(2025, 1, 1))) {
                continue;
            }

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

    private Mono<SyncBillSummaryResultDTO> syncBillSummaries() {

        // 삽입 : Bill 중 BillSummary 가 없는 Bill 들
        List<Bill> billsWithoutSummary = billService.getBillsWithoutSummary();

        return Flux.fromIterable(billsWithoutSummary)
                .delayElements(Duration.ofMillis(1333)) // 분당 45건 = 1.333초 간격
                .flatMap(bill -> {
                    try {
                        String scrapResult = billService.scrapData(bill.getDetailLink());

                        return congressApiClient.getBillSummaryResponse(scrapResult)
                                .map(dto -> {
                                    schedulerLogger.info("[BillSummary 동기화] billId : {} 요약 api 성공", bill.getId());
                                    return BillSummary.of(dto, bill);
                                })
                                .onErrorResume(e -> {
                                    schedulerLogger.error("[BillSummary 동기화] billId : {} 요약 api 실패: {}", bill.getId(), e.getMessage());
                                    return Mono.empty();
                                });

                    } catch (Exception e) {
                        log.error("[BillSummary 동기화] billId : {} 처리 중 예외 발생: {}", bill.getId(), e.getMessage());
                        return Mono.empty();
                    }
                })
                .buffer(billBatchInsertSize) // ✅ 100개 단위로 묶음
                .flatMap(batch -> {
                    try {
                        billSummaryService.saveAll(batch); // 저장
                        return Flux.fromIterable(batch);   // 다시 풀어서 내려보냄
                    } catch (Exception e) {
                        schedulerLogger.error("[BillSummary 동기화] batch 저장 실패: {}", e.getMessage());
                        return Flux.empty(); // 실패 시 그냥 버림
                    }
                })
                .collectList() // 모든 성공 저장된 BillSummary 모음
                .map(SyncBillSummaryResultDTO::of);
    }
}
