package com.guenbon.siso.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "댓글 조회 응답 dto")
public class CommentResponseDto {
    @Schema(description = "글 id")
    private String id;
    @Schema(description = "댓글 내용")
    private String content;
}
