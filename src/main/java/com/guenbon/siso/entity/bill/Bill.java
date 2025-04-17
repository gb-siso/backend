package com.guenbon.siso.entity.bill;

import com.guenbon.siso.entity.congressmanbill.CongressmanBill;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
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

    private LocalDateTime proposeDt; // PROPOSE_DT

    private String procResult; // PROC_RESULT (null 허용)

    private String age; // AGE 22만

    private String detailLink; // DETAIL_LINK (링크 주소)

    @Lob
    private String detailContent; // DETAIL_LINK에 들어가서 가져온 실제 내용

    private LocalDateTime lawProcDt; // LAW_PROC_DT

    private LocalDateTime lawPresentDt; // LAW_PRESENT_DT

    private LocalDateTime lawSubmitDt; // LAW_SUBMIT_DT

    private LocalDateTime cmtProcResultCd; // CMT_PROC_RESULT_CD

    private LocalDateTime cmtProcDt; // CMT_PROC_DT

    private LocalDateTime cmtPresentDt; // CMT_PRESENT_DT

    private LocalDateTime committeeDt; // COMMITTEE_DT

    private LocalDateTime procDt; // PROC_DT

    private String committeeId; // COMMITTEE_ID

    private String lawProcResultCd; // LAW_PROC_RESULT_CD

    // 발의자 (메인 발의자 + 공동 발의자)
    @OneToMany(mappedBy = "congressman")
    private List<CongressmanBill> congressmanBills = new ArrayList<>();
}

