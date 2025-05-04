package com.guenbon.siso.dto.bill.response;

import com.guenbon.siso.entity.bill.BillSummary;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class SyncBillSummaryResultDTO {
    private List<BillSummary> insertList;

    public static SyncBillSummaryResultDTO of(List<BillSummary> insertList) {
        return SyncBillSummaryResultDTO.builder().insertList(insertList).build();
    }
}
