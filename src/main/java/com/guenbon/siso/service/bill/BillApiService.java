package com.guenbon.siso.service.bill;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.CongressApiClient;
import com.guenbon.siso.dto.bill.BillScrapResult;
import com.guenbon.siso.dto.bill.BillSummaryParseResult;
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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.guenbon.siso.support.constants.ApiConstants.BILL_API_PATH;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillApiService {

    @Value("${bill.api.maxpage}")
    private int billApiMaxPage;
    private final CongressApiClient congressApiClient;
    private final BillService billService;
    private final BillSummaryService billSummaryService;

    public BillBatchResultDTO fetchAndSyncBillAndBillSummary() {
        SyncBillResultDTO syncBillResultDTO = fetchAndSyncBill();
        SyncBillSummaryResultDTO syncBillSummaryResultDTO = syncBillSummaries(syncBillResultDTO);
        return BillBatchResultDTO.of(syncBillResultDTO, syncBillSummaryResultDTO);
    }


    /**
     * 국회 api 에서 22대 국회 발의안 전체 목록을 가져와 db와 동기화한다
     *
     * @return
     */
    public SyncBillResultDTO fetchAndSyncBill() {
        List<Bill> apiBillList = new ArrayList<>();
        Map<String, List<String>> billProposerMap = new HashMap<>();

        int page = 1;

        while (page < billApiMaxPage) { // 혹시 모를 무한루프 방지
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

    public SyncBillSummaryResultDTO syncBillSummaries(SyncBillResultDTO syncBillResultDTO) {
        List<BillSummary> insertList = new ArrayList<>();
        int count = 0;

        for (Bill bill : syncBillResultDTO.getInsertList()) {
            BillScrapResult scrapResult = billService.scrapData(bill.getDetailLink());

            if (scrapResult.isSuccess()) {
                BillSummaryParseResult billSummaryParseResult = congressApiClient.getBillSummaryResponse(scrapResult.getContent());
                if (billSummaryParseResult.isSuccess()) {
                    insertList.add(BillSummary.of(billSummaryParseResult.getSummary(), bill));
                    log.info("[BillSummary 삽입] {} 번째 bill {} 요약 api 호출 성공", ++count, bill.getId());
                }
            } else {
                log.info("[BillSummary 삽입] {} 번째 bill {} 요약 api 호출 실패. 사유 : {}", ++count, bill.getId(), scrapResult.getErrorMessage());
            }
        }

        billSummaryService.saveAll(insertList);

        Map<Long, BillSummary> billSummaryMap = billSummaryService.getBillSummaryMap();
        List<BillSummary> updateList = new ArrayList<>();
        for (Bill bill : syncBillResultDTO.getUpdateList()) {
            BillScrapResult scrapResult = billService.scrapData(bill.getDetailLink());

            if (scrapResult.isSuccess()) {
                BillSummaryParseResult result = congressApiClient.getBillSummaryResponse(scrapResult.getContent());
                if (result.isSuccess()) {
                    BillSummary billSummary = billSummaryMap.get(bill.getId());
                    BillSummary updatedBillSummary = billSummaryService.updateFrom(billSummary, result.getSummary());
                    updateList.add(updatedBillSummary);
                    log.info("[BillSummary 수정] {} 번째 bill {} 요약 api 호출 성공", ++count, bill.getId());
                }
            } else {
                log.info("[BillSummary 수정] {} 번째 bill {} 요약 api 호출 실패. 사유 : {}", ++count, bill.getId(), scrapResult.getErrorMessage());
            }
        }

        // 트랜잭션 내에서 update
        billSummaryService.saveAll(updateList);

        // 삭제 : CASCADE 에 의해 처리

        return SyncBillSummaryResultDTO.of(insertList, updateList);
    }
}
