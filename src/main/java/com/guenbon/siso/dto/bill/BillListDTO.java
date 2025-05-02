package com.guenbon.siso.dto.bill;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class BillListDTO {
    private List<BillDTO> billList;
    private int page;
    private int lastPage;
}
