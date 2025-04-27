package com.guenbon.siso.dto.bill.response;

import com.guenbon.siso.entity.bill.Bill;
import lombok.*;

import java.util.List;

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

    public static BillBatchResultDTO of(List<Bill> insertList, List<Bill> deleteList, List<Bill> updateList) {
        return BillBatchResultDTO.builder().insertCount(insertList.size()).deleteCount(deleteList.size()).updateCount(updateList.size()).build();
    }
}
