package com.guenbon.siso.support.fixture.congressman;

import com.guenbon.siso.dto.congressman.CongressmanGetListDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CongressmanGetListDTOFixture {

    private Long id;
    private String name = "장지담";
    private Double rate = 5.0;
    private String timesElected = "10";
    private String party = "미래혁신당";
    // 추가된 필드
    private String code;
    private String position = "국회의원";
    private String electoralDistrict = "머임";
    private String electoralType = "머요";
    private List<Integer> assemblySessions = List.of(21, 22);
    private String sex = "남";
    private String imageUrl = "fake.com";

    public static CongressmanGetListDTOFixture builder() {
        return new CongressmanGetListDTOFixture();
    }

    // 기존 메서드
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

    public CongressmanGetListDTOFixture setTimesElected(String timesElected) {
        this.timesElected = timesElected;
        return this;
    }

    public CongressmanGetListDTOFixture setParty(String party) {
        this.party = party;
        return this;
    }

    // 새로 추가된 메서드
    public CongressmanGetListDTOFixture setCode(String code) {
        this.code = code;
        return this;
    }

    public CongressmanGetListDTOFixture setPosition(String position) {
        this.position = position;
        return this;
    }

    public CongressmanGetListDTOFixture setElectoralDistrict(String electoralDistrict) {
        this.electoralDistrict = electoralDistrict;
        return this;
    }

    public CongressmanGetListDTOFixture setElectoralType(String electoralType) {
        this.electoralType = electoralType;
        return this;
    }

    public CongressmanGetListDTOFixture setAssemblySessions(List<Integer> assemblySessions) {
        this.assemblySessions = assemblySessions;
        return this;
    }

    public CongressmanGetListDTOFixture setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public CongressmanGetListDTOFixture setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public CongressmanGetListDTO build() {
        return CongressmanGetListDTO.builder()
                .id(id)
                .name(name)
                .rate(rate)
                .timesElected(timesElected)
                .party(party)
                // 추가된 필드 빌더 체이닝
                .code(code)
                .position(position)
                .electoralDistrict(electoralDistrict)
                .electoralType(electoralType)
                .assemblySessions(assemblySessions)
                .sex(sex)
                .imageUrl(imageUrl)
                .build();
    }
}