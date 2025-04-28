package com.guenbon.siso.service.bill;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.CongressApiClient;
import com.guenbon.siso.dto.bill.BillSummaryDTO;
import com.guenbon.siso.dto.bill.response.BillBatchResultDTO;
import com.guenbon.siso.dto.bill.response.SyncBillResultDTO;
import com.guenbon.siso.entity.bill.Bill;
import com.guenbon.siso.entity.bill.BillSummary;
import com.guenbon.siso.factory.BillFactory;
import com.guenbon.siso.service.billsummary.BillSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final CongressApiClient congressApiClient;
    private final BillService billService;
    private final BillSummaryService billSummaryService;

    public BillBatchResultDTO fetchAndSyncBillAndBillSummary() {
        SyncBillResultDTO syncBillResultDTO = fetchAndSyncBill();
        syncBillSummaries(syncBillResultDTO);
        return BillBatchResultDTO.of(syncBillResultDTO);
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

        while (true) {
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

    public void syncBillSummaries(SyncBillResultDTO syncBillResultDTO) {
        // 요약 api -> BillSummary 삽입

        List<BillSummary> insertList = new ArrayList<>();

        for (Bill bill : syncBillResultDTO.getInsertList()) {
            // 상세 링크에서 스크랩해온 내용
            String data = billService.scrapData(bill.getDetailLink());
            // 스크랩해온 내용으로 요약 api 쏘기
            BillSummaryDTO billSummaryDTO = congressApiClient.getBillSummaryResponse(data);
            insertList.add(BillSummary.of(billSummaryDTO, bill));
        }

        // 배치 삽입
        billSummaryService.saveAll(insertList);

        Map<Long, BillSummary> billSummaryMap = billSummaryService.getBillSummaryMap();
        // 요약 api -> BillSummary 업데이트
        for (Bill bill : syncBillResultDTO.getUpdateList()) {
            // 상세 링크에서 스크랩해온 내용
            String data = billService.scrapData(bill.getDetailLink());
            // 스크랩해온 내용으로 요약 api 쏘기
            BillSummaryDTO billSummaryDTO = congressApiClient.getBillSummaryResponse(data);
            // 수정하기
            BillSummary billSummary = billSummaryMap.get(bill.getId());
            billSummaryService.updateFrom(billSummary, billSummaryDTO);
        }
    }
}
