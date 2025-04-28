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
public class SyncBillResultDTO {
    private List<Bill> insertList;
    private List<Bill> updateList;
    private List<Bill> deleteList;

    public static SyncBillResultDTO of(List<Bill> insertList, List<Bill> deleteList, List<Bill> updateList) {
        return SyncBillResultDTO.builder().insertList(insertList).deleteList(deleteList).updateList(updateList).build();
    }
}
