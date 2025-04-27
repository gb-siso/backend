package com.guenbon.siso.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.entity.bill.Bill;

import java.time.LocalDate;

public class BillFactory {

    /**
     * 국회 API 응답에서 Bill 객체로 변환하는 팩토리 메서드
     */
    public static Bill from(JsonNode billNode) {
        return Bill.builder()
                .billId(billNode.path("BILL_ID").asText(null))
                .billNo(billNode.path("BILL_NO").asText(null))
                .billName(billNode.path("BILL_NAME").asText(null))
                .committee(billNode.path("COMMITTEE").asText(null))
                .proposeDt(parseDate(billNode.path("PROPOSE_DT").asText(null)))
                .procResult(billNode.path("PROC_RESULT").asText(null))
                .age(billNode.path("AGE").asText(null))
                .detailLink(billNode.path("DETAIL_LINK").asText(null))
                .lawProcDt(parseDate(billNode.path("LAW_PROC_DT").asText(null)))
                .lawPresentDt(parseDate(billNode.path("LAW_PRESENT_DT").asText(null)))
                .lawSubmitDt(parseDate(billNode.path("LAW_SUBMIT_DT").asText(null)))
                .cmtProcResultCd(billNode.path("CMT_PROC_RESULT_CD").asText(null))
                .cmtProcDt(parseDate(billNode.path("CMT_PROC_DT").asText(null)))
                .cmtPresentDt(parseDate(billNode.path("CMT_PRESENT_DT").asText(null)))
                .committeeDt(parseDate(billNode.path("COMMITTEE_DT").asText(null)))
                .procDt(parseDate(billNode.path("PROC_DT").asText(null)))
                .committeeId(billNode.path("COMMITTEE_ID").asText(null))
                .lawProcResultCd(billNode.path("LAW_PROC_RESULT_CD").asText(null))
                .build();
    }

    /**
     * 날짜 문자열을 LocalDate로 파싱하는 유틸 메서드
     */
    private static LocalDate parseDate(String dateStr) {
        return (dateStr == null || dateStr.isBlank()) ? null : LocalDate.parse(dateStr.trim());
    }
}
