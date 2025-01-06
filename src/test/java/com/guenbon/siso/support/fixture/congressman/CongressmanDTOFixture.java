package com.guenbon.siso.support.fixture.congressman;


import com.guenbon.siso.dto.congressman.response.CongressmanDetailDTO.CongressmanDTO;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CongressmanDTOFixture {
    private String id;
    private String name = "윤석열";
    private String party = "국민의힘";
    private Integer timesElected = 1;
    private Double rate;
    private List<String> ratedMemberImages = List.of("탄핵.jpg", "계엄.jpg", "당선.jpg");

    public static CongressmanDTOFixture builder() {
        return new CongressmanDTOFixture();
    }

    public CongressmanDTOFixture setId(String id) {
        this.id = id;
        return this;
    }

    public CongressmanDTOFixture setName(String name) {
        this.name = name;
        return this;
    }

    public CongressmanDTOFixture setParty(String party) {
        this.party = party;
        return this;
    }

    public CongressmanDTOFixture setTimesElected(Integer timesElected) {
        this.timesElected = timesElected;
        return this;
    }

    public CongressmanDTOFixture setRate(Double rate) {
        this.rate = rate;
        return this;
    }

    public CongressmanDTOFixture setRatedMemberImages(List<String> ratedMemberImages) {
        this.ratedMemberImages = ratedMemberImages;
        return this;
    }

    public CongressmanDTO build() {
        return new CongressmanDTO(id, name, party, timesElected, rate, ratedMemberImages);
    }
}
