package com.guenbon.siso.dto.congressman.response;

import com.guenbon.siso.dto.congressman.CongressmanGetListDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "국회의원 목록 응답 dto")
public class CongressmanListDTO {
    @Schema(description = "국회의원 목록")
    private List<CongressmanDTO> congressmanList;
    @Schema(description = "무한스크롤 커서값")
    private String idCursor;
    private Double rateCursor;
    @Schema(description = "무한스크롤 마지막 데이터 여부")
    private Boolean lastPage;

    @Override
    public String toString() {
        return "CongressmanListDTO{" +
                "congressmanList=" + congressmanList +
                ", idCursor='" + idCursor + '\'' +
                ", rateCursor=" + rateCursor +
                ", lastPage=" + lastPage +
                '}';
    }

    @NoArgsConstructor
    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "국회의원 목록 응답 dto")
    public static class CongressmanDTO {
        private String id;
        private String name;
        private String party;
        private String timesElected;
        private Double rate;
        private String code;
        private String position;
        private String electoralDistrict;
        private String electoralType;
        private List<Integer> assemblySessions;
        private String sex;
        private String imageUrl;
        @Schema(description = "해당 의원 최근에 평가한 회원 4명 이미지")
        private List<String> ratedMemberImages;



        public static CongressmanDTO of(String encryptedCongressmanId, CongressmanGetListDTO congressmanGetListDTO,
                                        List<String> memberImages) {
            return CongressmanDTO.builder()
                    .id(encryptedCongressmanId)
                    .name(congressmanGetListDTO.getName())
                    .rate(congressmanGetListDTO.getRate())
                    .party(congressmanGetListDTO.getParty())
                    .timesElected(congressmanGetListDTO.getTimesElected())
                    .ratedMemberImages(memberImages == null ? Collections.emptyList() : memberImages)
                    .code(congressmanGetListDTO.getCode())
                    .position(congressmanGetListDTO.getPosition())
                    .electoralDistrict(congressmanGetListDTO.getElectoralDistrict())
                    .electoralType(congressmanGetListDTO.getElectoralType())
                    .assemblySessions(congressmanGetListDTO.getAssemblySessions())
                    .sex(congressmanGetListDTO.getSex())
                    .imageUrl(congressmanGetListDTO.getImageUrl())
                    .build();
        }
    }
}
