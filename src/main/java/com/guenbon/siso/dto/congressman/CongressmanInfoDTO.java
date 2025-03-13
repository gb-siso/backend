package com.guenbon.siso.dto.congressman;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CongressmanInfoDTO {
    private final String code;             // 국회의원 코드
    private final String name;             // 국회의원 한글 이름
    private final String position;         // 직책
    private final String party;            // 소속 정당
    private final String electoralDistrict; // 선거구
    private final String electoralType;    // 선거구 구분
    private final String termsInOffice;    // 당선 횟수
    private final String assemblySessions; // 국회의원 대수
    private final String sex;              // 성별
    private final String imageUrl;         // 사진 URL

    @Builder
    public CongressmanInfoDTO(
            final String code, final String name, final String position,
            final String party, final String electoralDistrict, final String electoralType,
            final String termsInOffice, final String assemblySessions, final String sex,
            final String imageUrl) {
        this.code = code;
        this.name = name;
        this.position = position;
        this.party = party;
        this.electoralDistrict = electoralDistrict;
        this.electoralType = electoralType;
        this.termsInOffice = termsInOffice;
        this.assemblySessions = assemblySessions;
        this.sex = sex;
        this.imageUrl = imageUrl;
    }

    public static CongressmanInfoDTO of(JsonNode row, String assemblySessions) {
        return CongressmanInfoDTO.builder()
                .code(row.path("NAAS_CD").asText())
                .name(row.path("NAAS_NM").asText())
                .position(row.path("DTY_NM").asText(null))
                .party(row.path("PLPT_NM").asText(null))
                .electoralDistrict(row.path("ELECD_NM").asText(null))
                .electoralType(row.path("ELECD_DIV_NM").asText(null))
                .termsInOffice(row.path("RLCT_DIV_NM").asText(null))
                .assemblySessions(assemblySessions)
                .sex(row.path("NTR_DIV").asText(null))
                .imageUrl(row.path("NAAS_PIC").asText(null))
                .build();
    }
}


