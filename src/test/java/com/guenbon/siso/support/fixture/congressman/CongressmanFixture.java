package com.guenbon.siso.support.fixture.congressman;

import com.guenbon.siso.entity.Congressman;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class CongressmanFixture {

    private String code = "ABC123";
    private Long id;
    private String name = "이준석";
    private String party = "국민의힘";
    private String timesElected = "3";
    private String position = "국회의원";
    private String electoralDistrict = "서울 강남구";
    private String electoralType = "지역구";
    private List<Integer> assemblySessions = List.of(22);
    private String sex = "남성";
    private String imageUrl = "https://example.com/profile.jpg";

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

    public CongressmanFixture setPosition(String position) {
        this.position = position;
        return this;
    }

    public CongressmanFixture setElectoralDistrict(String electoralDistrict) {
        this.electoralDistrict = electoralDistrict;
        return this;
    }

    public CongressmanFixture setElectoralType(String electoralType) {
        this.electoralType = electoralType;
        return this;
    }

    public CongressmanFixture setAssemblySessions(List<Integer> assemblySessions) {
        this.assemblySessions = assemblySessions;
        return this;
    }

    public CongressmanFixture setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public CongressmanFixture setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public Congressman build() {
        return Congressman.builder()
                .code(code)
                .id(id)
                .name(name)
                .party(party)
                .timesElected(timesElected)
                .position(position)
                .electoralDistrict(electoralDistrict)
                .electoralType(electoralType)
                .assemblySessions(assemblySessions)
                .sex(sex)
                .imageUrl(imageUrl)
                .build();
    }

    public static Congressman fromId(Long id) {
        return CongressmanFixture.builder()
                .setId(id)
                .build();
    }
}
