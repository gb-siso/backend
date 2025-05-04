package com.guenbon.siso.dto.bill.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class BillBatchResultDTO {
    private int billInsertCount;
    private int billUpdateCount;
    private int billDeleteCount;
    private int billSummaryInsertCount;

    public static BillBatchResultDTO of(SyncBillResultDTO syncBillResultDTO, SyncBillSummaryResultDTO syncBillSummaryResultDTO) {
        return BillBatchResultDTO.builder().billInsertCount(syncBillResultDTO.getInsertList().size())
                .billDeleteCount(syncBillResultDTO.getDeleteList().size())
                .billUpdateCount(syncBillResultDTO.getUpdateList().size())
                .billSummaryInsertCount(syncBillSummaryResultDTO.getInsertList().size())
                .build();
    }
}