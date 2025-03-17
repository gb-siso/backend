package com.guenbon.siso.support.fixture.congressman;

import com.guenbon.siso.entity.Congressman;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CongressmanFixture { // todo 신규 필드 추가 (테스트 꺠짐)

    private String code = "ABC123";

    private Long id;

    private String name = "이준석";

    private String party = "국민의힘";

    private String timesElected = "3";

    public static CongressmanFixture builder() {
        return new CongressmanFixture();
    }

    public CongressmanFixture setCode(String code) {
        this.code = code;
        return this;
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

    public CongressmanFixture setTimesElected(String timesElected) {
        this.timesElected = timesElected;
        return this;
    }

    public Congressman build() {
        return Congressman.builder()
                .code(code)
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

    public static Congressman fromId(Long id) {
        return CongressmanFixture.builder()
                .setId(id)
                .build();
    }
}
