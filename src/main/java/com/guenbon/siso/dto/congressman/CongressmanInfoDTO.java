package com.guenbon.siso.dto.congressman;

import com.fasterxml.jackson.databind.JsonNode;
import com.guenbon.siso.entity.Congressman;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@ToString
public class CongressmanInfoDTO {
    private final String code;             // 국회의원 코드
    private final String name;             // 국회의원 한글 이름
    private final String position;         // 직책
    private final String party;            // 소속 정당
    private final String electoralDistrict; // 선거구
    private final String electoralType;    // 선거구 구분
    private final String timesElected;    // 당선 횟수
    private final List<Integer> assemblySessions; // 국회의원 대수
    private final String sex;              // 성별
    private final String imageUrl;         // 사진 URL

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // 주소값이 같으면 동일 객체
        if (o == null || getClass() != o.getClass()) return false; // 타입이 다르면 false
        CongressmanInfoDTO that = (CongressmanInfoDTO) o;
        return Objects.equals(code, that.code) &&
                Objects.equals(name, that.name) &&
                Objects.equals(position, that.position) &&
                Objects.equals(party, that.party) &&
                Objects.equals(electoralDistrict, that.electoralDistrict) &&
                Objects.equals(electoralType, that.electoralType) &&
                Objects.equals(timesElected, that.timesElected) &&
                Objects.equals(assemblySessions, that.assemblySessions) &&
                Objects.equals(sex, that.sex) &&
                Objects.equals(imageUrl, that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                code, name, position, party, electoralDistrict,
                electoralType, timesElected, assemblySessions, sex, imageUrl
        );
    }

    @Builder
    public CongressmanInfoDTO(
            final String code, final String name, final String position,
            final String party, final String electoralDistrict, final String electoralType,
            final String timesElected, final List<Integer> assemblySessions, final String sex,
            final String imageUrl) {
        this.code = code;
        this.name = name;
        this.position = position;
        this.party = party;
        this.electoralDistrict = electoralDistrict;
        this.electoralType = electoralType;
        this.timesElected = timesElected;
        this.assemblySessions = assemblySessions;
        this.sex = sex;
        this.imageUrl = imageUrl;
    }

    public static CongressmanInfoDTO from(Congressman congressman) {
        return CongressmanInfoDTO.builder()
                .code(congressman.getCode())
                .name(congressman.getName())
                .position(congressman.getPosition())
                .party(congressman.getParty())
                .electoralDistrict(congressman.getElectoralDistrict())
                .electoralType(congressman.getElectoralType())
                .timesElected(congressman.getTimesElected())
                .assemblySessions(congressman.getAssemblySessions())
                .sex(congressman.getSex())
                .imageUrl(congressman.getImageUrl())
                .build();
    }

    public static CongressmanInfoDTO of(JsonNode row, String assemblySessions) {
        return CongressmanInfoDTO.builder()
                .code(row.path("NAAS_CD").asText())
                .name(row.path("NAAS_NM").asText())
                .position(row.path("DTY_NM").asText(null))
                .party(row.path("PLPT_NM").asText(null))
                .electoralDistrict(row.path("ELECD_NM").asText(null))
                .electoralType(row.path("ELECD_DIV_NM").asText(null))
                .timesElected(row.path("RLCT_DIV_NM").asText(null))
                .assemblySessions(parseAssemblySessions(assemblySessions))
                .sex(row.path("NTR_DIV").asText(null))
                .imageUrl(row.path("NAAS_PIC").asText(null))
                .build();
    }

    public static List<Integer> parseAssemblySessions(String assemblySessions) {
        List<Integer> sessions = new ArrayList<>();

        // 정규표현식으로 "제XX대" 형식의 문자열을 찾음
        Pattern pattern = Pattern.compile("제(\\d+)대");
        Matcher matcher = pattern.matcher(assemblySessions);

        // 매칭된 부분에서 숫자만 추출하여 List에 추가
        while (matcher.find()) {
            int sessionNumber = Integer.parseInt(matcher.group(1)); // 숫자 부분 추출
            sessions.add(sessionNumber);
        }

        return sessions;
    }
}


