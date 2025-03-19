package com.guenbon.siso.support.fixture.congressman;


import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CongressmanDTOFixture {
    private String id;
    private String name = "윤석열";
    private String party = "국민의힘";
    private String timesElected = "1";
    private Double rate;
    private String code = "CODE123";
    private String position = "의원";
    private String electoralDistrict = "서울 강남구";
    private String electoralType = "비례대표";
    private List<Integer> assemblySessions = List.of(21, 22);
    private String sex = "남성";
    private String imageUrl = "image_url.jpg";
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

    public CongressmanDTOFixture setTimesElected(String timesElected) {
        this.timesElected = timesElected;
        return this;
    }

    public CongressmanDTOFixture setRate(Double rate) {
        this.rate = rate;
        return this;
    }

    public CongressmanDTOFixture setCode(String code) {
        this.code = code;
        return this;
    }

    public CongressmanDTOFixture setPosition(String position) {
        this.position = position;
        return this;
    }

    public CongressmanDTOFixture setElectoralDistrict(String electoralDistrict) {
        this.electoralDistrict = electoralDistrict;
        return this;
    }

    public CongressmanDTOFixture setElectoralType(String electoralType) {
        this.electoralType = electoralType;
        return this;
    }

    public CongressmanDTOFixture setAssemblySessions(List<Integer> assemblySessions) {
        this.assemblySessions = assemblySessions;
        return this;
    }

    public CongressmanDTOFixture setSex(String sex) {
        this.sex = sex;
        return this;
    }

    public CongressmanDTOFixture setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public CongressmanDTOFixture setRatedMemberImages(List<String> ratedMemberImages) {
        this.ratedMemberImages = ratedMemberImages;
        return this;
    }

    public CongressmanDTO build() {
        return new CongressmanDTO(id, name, party, timesElected, rate, code, position, electoralDistrict, electoralType, assemblySessions, sex, imageUrl, ratedMemberImages);
    }
}

