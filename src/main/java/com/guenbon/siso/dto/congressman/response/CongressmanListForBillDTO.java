package com.guenbon.siso.dto.congressman.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class CongressmanListForBillDTO {
    private String congressmanId;
    private String name;

    public static CongressmanListForBillDTO from(String encryptedCongressmanId, String name) {
        return new CongressmanListForBillDTO(encryptedCongressmanId, name);
    }
}
