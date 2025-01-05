package com.guenbon.siso.support.fixture.congressman;

import com.guenbon.siso.entity.Congressman;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CongressmanFixture {

    private Long id;

    private String name = "이준석";

    private String party = "국민의힘";

    private Integer timesElected = 3;

    public static CongressmanFixture builder() {
        return new CongressmanFixture();
    }

    public CongressmanFixture setId(Long id) {
        this.id = id;
        return this;
    }

    public CongressmanFixture setName(String name) {
        this.name = name;
        return this;
    }

    public CongressmanFixture setParty(String party) {
        this.party = party;
        return this;
    }

    public CongressmanFixture setTimesElected(Integer timesElected) {
        this.timesElected = timesElected;
        return this;
    }

    public Congressman build() {
        return Congressman.builder()
                .id(id)
                .name(name)
                .party(party)
                .timesElected(timesElected)
                .build();
    }

    @Override
    public String toString() {
        return "CongressmanFixture{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", party='" + party + '\'' +
                ", timesElected=" + timesElected +
                '}';
    }
}
