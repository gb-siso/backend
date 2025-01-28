package com.guenbon.siso.dto.bill;

import static com.guenbon.siso.support.constants.ApiConstants.BILL_NAME;
import static com.guenbon.siso.support.constants.ApiConstants.DETAIL_LINK;
import static com.guenbon.siso.support.constants.ApiConstants.PROPOSER;
import static com.guenbon.siso.support.constants.ApiConstants.PROPOSE_DT;
import static com.guenbon.siso.support.constants.ApiConstants.PUBL_PROPOSER;
import static com.guenbon.siso.support.constants.ApiConstants.RST_PROPOSER;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;

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

    public static BillListDTO of(JsonNode bills, int lastPage) {
        final List<BillDTO> billDTOList = new ArrayList<>();
        for (final JsonNode bill : bills) {
            final String rawTitle = bill.path(BILL_NAME).asText();
            final String title = StringEscapeUtils.unescapeHtml4(rawTitle);
            final String link = bill.path(DETAIL_LINK).asText();
            final String proposeDate = bill.path(PROPOSE_DT).asText();
            final String proposer = bill.path(PROPOSER).asText();
            final String publProposer = bill.path(PUBL_PROPOSER).asText();
            final String rstProposer = bill.path(RST_PROPOSER).asText();

            final BillDTO billDTO = BillDTO.of(title, proposer, publProposer, rstProposer, link, proposeDate);
            billDTOList.add(billDTO);
        }
        return BillListDTO.of(billDTOList, lastPage);
    }
}
