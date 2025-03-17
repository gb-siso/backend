package com.guenbon.siso.entity;


import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.entity.common.DateEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Congressman extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String party;

    @Column(nullable = false)
    private String timesElected;

    // 국회의원 코드
    @Column(nullable = false, unique = true)
    private String code;

    // 직책
    private String position;

    // 소속 정당
    private String electoralDistrict;

    // 선거구
    private String electoralType;

    // 선거구 구분
    @ElementCollection
    @Column(name = "assembly_sessions")
    @CollectionTable(name = "assembly_session", joinColumns = @JoinColumn(name = "congressman_id"))
    private List<Integer> assemblySessions;

    private String sex;

    private String imageUrl;

    @Override
    public String toString() {
        return "Congressman{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", party='" + party + '\'' +
                ", timesElected=" + timesElected +
                '}';
    }

    public static Congressman of(JsonNode row, List<Integer> assemblySessions) {
        return Congressman.builder()
                .code(row.path("NAAS_CD").asText())
                .name(row.path("NAAS_NM").asText())
                .position(row.path("DTY_NM").asText(null))
                .party(row.path("PLPT_NM").asText(null))
                .electoralDistrict(row.path("ELECD_NM").asText(null))
                .electoralType(row.path("ELECD_DIV_NM").asText(null))
                .timesElected(row.path("RLCT_DIV_NM").asText(null))
                .assemblySessions(assemblySessions)
                .sex(row.path("NTR_DIV").asText(null))
                .imageUrl(row.path("NAAS_PIC").asText(null))
                .build();
    }

    public Congressman updateFieldsFrom(Congressman other) {
        if (other == null) {
            return this;  // 다른 객체가 null이면 자기 자신을 그대로 반환
        }

        this.name = other.getName();
        this.party = other.getParty();
        this.timesElected = other.getTimesElected();
        this.code = other.getCode();
        this.position = other.getPosition();
        this.electoralDistrict = other.getElectoralDistrict();
        this.electoralType = other.getElectoralType();
        this.assemblySessions = other.getAssemblySessions();
        this.sex = other.getSex();
        this.imageUrl = other.getImageUrl();

        return this;  // 자기 자신을 반환
    }
}
