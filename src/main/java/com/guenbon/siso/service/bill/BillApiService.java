package com.guenbon.siso.service.bill;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.client.CongressApiClient;
import com.guenbon.siso.dto.bill.response.BillBatchResultDTO;
import com.guenbon.siso.entity.bill.Bill;
import com.guenbon.siso.factory.BillFactory;
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

    public BillBatchResultDTO fetchAndSyncBill() {
        List<Bill> apiBillList = new ArrayList<>();
        Map<String, List<String>> billProposerMap = new HashMap<>();

        int page = 1;

        while (true) {
            log.info("page  : {}", page);

            final JsonNode jsonNode = congressApiClient.getBillResponse(page++);

            if (congressApiClient.isApiResponseError(jsonNode)) {
                if (congressApiClient.isLastPage(jsonNode)) break;
                congressApiClient.handleApiError(jsonNode);
            }

            final JsonNode billRowNodeList = congressApiClient.getContent(jsonNode, BILL_API_PATH);

            extractBillsFromApiResponse(billRowNodeList, billProposerMap, apiBillList);
        }

        log.info("api 요청 후 발의안 목록 가져오기 완료");


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
}
