package com.guenbon.siso.dto.bill.response;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class BillBatchResultDTO {
    private int insertCount;
    private int updateCount;
    private int deleteCount;

    public static BillBatchResultDTO of(SyncBillResultDTO syncBillResultDTO) {
        return BillBatchResultDTO.builder().insertCount(syncBillResultDTO.getInsertList().size())
                .deleteCount(syncBillResultDTO.getDeleteList().size())
                .updateCount(syncBillResultDTO.getUpdateList().size()).build();
    }
}