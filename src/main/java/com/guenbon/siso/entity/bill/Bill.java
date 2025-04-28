package com.guenbon.siso.entity.bill;

import com.guenbon.siso.entity.congressmanbill.CongressmanBill;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String billId; // BILL_ID

    @Column(unique = true, nullable = false)
    private String billNo; // BILL_NO

    @Column(nullable = false)
    private String billName; // BILL_NAME

    private String committee; // COMMITTEE

    private LocalDate proposeDt; // PROPOSE_DT

    private String procResult; // PROC_RESULT (null 허용)

    private String age; // AGE 22만

    private String detailLink; // DETAIL_LINK (링크 주소)

    private LocalDate lawProcDt; // LAW_PROC_DT

    private LocalDate lawPresentDt; // LAW_PRESENT_DT

    private LocalDate lawSubmitDt; // LAW_SUBMIT_DT

    private String cmtProcResultCd; // CMT_PROC_RESULT_CD

    private LocalDate cmtProcDt; // CMT_PROC_DT

    private LocalDate cmtPresentDt; // CMT_PRESENT_DT

    private LocalDate committeeDt; // COMMITTEE_DT

    private LocalDate procDt; // PROC_DT

    private String committeeId; // COMMITTEE_ID

    private String lawProcResultCd; // LAW_PROC_RESULT_CD

    // 발의자 (메인 발의자 + 공동 발의자)
    @Setter
    @OneToMany(mappedBy = "bill", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @Builder.Default
    private List<CongressmanBill> congressmanBills = new ArrayList<>();

    @Setter
    @OneToOne(mappedBy = "bill", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private BillSummary billSummary;

    public void updateFrom(Bill from) {
        this.billId = from.billId; // BILL_ID 업데이트
        this.billNo = from.billNo; // BILL_NO 업데이트
        this.billName = from.billName; // BILL_NAME 업데이트
        this.committee = from.committee; // COMMITTEE 업데이트
        this.proposeDt = from.proposeDt; // PROPOSE_DT 업데이트
        this.procResult = from.procResult; // PROC_RESULT 업데이트
        this.age = from.age; // AGE 업데이트
        this.detailLink = from.detailLink; // DETAIL_LINK 업데이트
        this.lawProcDt = from.lawProcDt; // LAW_PROC_DT 업데이트
        this.lawPresentDt = from.lawPresentDt; // LAW_PRESENT_DT 업데이트
        this.lawSubmitDt = from.lawSubmitDt; // LAW_SUBMIT_DT 업데이트
        this.cmtProcResultCd = from.cmtProcResultCd; // CMT_PROC_RESULT_CD 업데이트
        this.cmtProcDt = from.cmtProcDt; // CMT_PROC_DT 업데이트
        this.cmtPresentDt = from.cmtPresentDt; // CMT_PRESENT_DT 업데이트
        this.committeeDt = from.committeeDt; // COMMITTEE_DT 업데이트
        this.procDt = from.procDt; // PROC_DT 업데이트
        this.committeeId = from.committeeId; // COMMITTEE_ID 업데이트
        this.lawProcResultCd = from.lawProcResultCd; // LAW_PROC_RESULT_CD 업데이트
    }
}

