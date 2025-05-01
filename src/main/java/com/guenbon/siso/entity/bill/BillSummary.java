package com.guenbon.siso.entity.bill;

import com.guenbon.siso.dto.bill.BillSummaryDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bill_id")
    private Bill bill;

    // 이하는 요약 api 로 만든 정보들
    private String category;
    private String content;
    private String reason;
    private String expected;

    public static BillSummary of(BillSummaryDTO billSummaryDTO, Bill bill) {
        return BillSummary.builder().bill(bill)
                .category(billSummaryDTO.getCategory())
                .reason(billSummaryDTO.getReason())
                .content(billSummaryDTO.getContent())
                .expected(billSummaryDTO.getExpected())
                .build();
    }

    public BillSummary updateFrom(BillSummaryDTO billSummaryDTO) {
        this.category = billSummaryDTO.getCategory();
        this.content = billSummaryDTO.getContent();
        this.reason = billSummaryDTO.getReason();
        this.expected = billSummaryDTO.getExpected();
        return this;
    }
}
