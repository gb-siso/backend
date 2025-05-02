package com.guenbon.siso.dto.bill.projection;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BillListProjectionDTO {

    // 발의안 내용
    private final Long billId; // 주의 : bill.id (bill.bill_id 아님)
    private final String billNo;
    private final String billName;
    private final String committee;
    private final LocalDate proposeDt;
    private final String procResult;
    private final String age;
    private final String detailLink;
    private final LocalDate lawProcDt;
    private final LocalDate lawPresentDt;
    private final LocalDate lawSubmitDt;
    private final String cmtProcResultCd;
    private final LocalDate cmtProcDt;
    private final LocalDate cmtPresentDt;
    private final LocalDate committeeDt;
    private final LocalDate procDt;
    private final String committeeId;
    private final String lawProcResultCd;

    // 요약 내용
    private final String category;
    private final String content;
    private final String reason;
    private final String expected;

    public BillListProjectionDTO(Long billId, String billNo, String billName, String committee,
                                 LocalDate proposeDt, String procResult, String age, String detailLink,
                                 LocalDate lawProcDt, LocalDate lawPresentDt, LocalDate lawSubmitDt,
                                 String cmtProcResultCd, LocalDate cmtProcDt, LocalDate cmtPresentDt,
                                 LocalDate committeeDt, LocalDate procDt, String committeeId, String lawProcResultCd,
                                 String category, String content, String reason, String expected) {
        this.billId = billId;
        this.billNo = billNo;
        this.billName = billName;
        this.committee = committee;
        this.proposeDt = proposeDt;
        this.procResult = procResult;
        this.age = age;
        this.detailLink = detailLink;
        this.lawProcDt = lawProcDt;
        this.lawPresentDt = lawPresentDt;
        this.lawSubmitDt = lawSubmitDt;
        this.cmtProcResultCd = cmtProcResultCd;
        this.cmtProcDt = cmtProcDt;
        this.cmtPresentDt = cmtPresentDt;
        this.committeeDt = committeeDt;
        this.procDt = procDt;
        this.committeeId = committeeId;
        this.lawProcResultCd = lawProcResultCd;

        this.category = category;
        this.content = content;
        this.reason = reason;
        this.expected = expected;
    }
}
