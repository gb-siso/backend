package com.guenbon.siso.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "글 조회 응답 dto")
public class ArticleResponseDto {
    @Schema(description = "글 id")
    private String id;
    @Schema(description = "글 제목")
    private String title;
    @Schema(description = "글 내용")
    private String content;
}
