package com.guenbon.siso.dto.congressman.response;

import com.guenbon.siso.dto.congressman.common.CongressmanDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "국회의원 목록 응답 dto")
public class CongressmanListDTO {
    @Schema(description = "국회의원 목록")
    private List<CongressmanDTO> congressmanList;
    @Schema(description = "무한스크롤 커서값")
    private Long cursor;
    @Schema(description = "무한스크롤 마지막 데이터 여부")
    private Boolean lastPage;
    @Schema(description = "해당 의원 최근에 평가한 회원 4명 이미지")
    private List<String> ratedMemberImages;
}
