package com.guenbon.siso.support.fixture;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CongressmanGetListDTOFixture {

    private Long id;
    private String name = "장지담";
    private Double rate;
    private Integer timesElected = 10;
    private String party = "미래혁신당";

    public static CongressmanGetListDTOFixture builder() {
        return new CongressmanGetListDTOFixture();
    }

    public CongressmanGetListDTOFixture setId(Long id) {
        this.id = id;
        return this;
    }

    public CongressmanGetListDTOFixture setName(String name) {
        this.name = name;
        return this;
    }

    public CongressmanGetListDTOFixture setRate(Double rate) {
        this.rate = rate;
        return this;
    }

    public CongressmanGetListDTOFixture setTimesElected(Integer timesElected) {
        this.timesElected = timesElected;
        return this;
    }

    public CongressmanGetListDTOFixture setParty(String party) {
        this.party = party;
        return this;
    }

    public CongressmanGetListDTO build() {
        return CongressmanGetListDTO.builder()
                .id(id)
                .name(name)
                .rate(rate)
                .timesElected(timesElected)
                .party(party)
                .build();
    }
}
