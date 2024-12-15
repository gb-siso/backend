package com.guenbon.siso.dto.rating;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "평점 조회 응답 dto")
public class RatingResponseDto {
    @Schema(description = "평점 대상 id")
    private String targetId;
    @Schema(description = "평점 대상 이름")
    private String targetName;
    @Schema(description = "평점 id")
    private String id;
    @Schema(description = "평점")
    private Float rate;
}
