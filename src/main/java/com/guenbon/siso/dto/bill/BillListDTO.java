package com.guenbon.siso.dto.bill;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BillListDTO {
    private List<BillDTO> billList;
    private int lastPage;

    public static BillListDTO of(List<BillDTO> billList, int lastPage) {
        return new BillListDTO(billList, lastPage);
    }
}
