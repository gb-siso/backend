package com.guenbon.siso.dto.congressman.projection;

import lombok.ToString;

@ToString
public class CongressmanBillListDTO {
    private Long id;
    private String name;

    public CongressmanBillListDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
