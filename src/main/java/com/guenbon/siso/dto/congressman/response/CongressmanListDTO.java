package com.guenbon.siso.dto.congressman.response;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        private Integer timesElected;
        private Double rate;
        @Schema(description = "해당 의원 최근에 평가한 회원 4명 이미지")
        private List<String> ratedMemberImages;

        public static CongressmanDTO of(String encryptedCongressmanId, CongressmanGetListDTO congressmanGetListDTO,
                                        List<String> memberImages) {
            if (encryptedCongressmanId == null || congressmanGetListDTO == null) {
                throw new InternalServerException(CommonErrorCode.NULL_VALUE_NOT_ALLOWED);
            }
            return CongressmanDTO.builder()
                    .id(encryptedCongressmanId)
                    .name(congressmanGetListDTO.getName())
                    .rate(congressmanGetListDTO.getRate())
                    .party(congressmanGetListDTO.getParty())
                    .timesElected(congressmanGetListDTO.getTimesElected())
                    .ratedMemberImages(memberImages == null ? Collections.emptyList() : memberImages)
                    .build();
        }
    }
}
