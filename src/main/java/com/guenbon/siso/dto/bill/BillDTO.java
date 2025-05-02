package com.guenbon.siso.dto.bill;

import com.guenbon.siso.dto.bill.projection.BillListProjectionDTO;
import com.guenbon.siso.dto.congressman.projection.CongressmanBillListDTO;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class BillDTO {
    private BillListProjectionDTO billListProjectionDTO;
    private List<CongressmanBillListDTO> congressmanBillListDTOList;
}
