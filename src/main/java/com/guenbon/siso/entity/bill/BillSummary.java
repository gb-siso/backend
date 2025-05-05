package com.guenbon.siso.entity.bill;

import com.guenbon.siso.dto.bill.BillSummaryDTO;
import jakarta.persistence.*;
import lombok.*;

import java.util.Optional;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
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
        // perplexity api 가 공백, * 롤 포함해서 category 를 응답해서 처리하기 위함
        final String cleanedCategory = Optional.ofNullable(billSummaryDTO.getCategory())
                .map(str -> str.replace("*", "").trim())
                .orElse(null);

        return BillSummary.builder()
                .bill(bill)
                .category(cleanedCategory)
                .reason(billSummaryDTO.getReason())
                .content(billSummaryDTO.getContent())
                .expected(billSummaryDTO.getExpected())
                .build();
    }

}
