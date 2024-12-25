package com.guenbon.siso.dto.rating.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Schema(description = "평가 작성 요청 dto")
@Builder
@AllArgsConstructor
public class RatingWriteDTO {
    @Schema(description = "작성 대상 국회의원 id")
    private String congressmanId;
    private String content;
    private Float rating;
}
