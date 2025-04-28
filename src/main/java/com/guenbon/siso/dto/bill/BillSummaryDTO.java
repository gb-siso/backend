package com.guenbon.siso.dto.bill;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class BillSummaryDTO {
    private String category;
    private String content;
    private String reason;
    private String expected;
}
