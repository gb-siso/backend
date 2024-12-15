package com.guenbon.siso.dto.member;

import com.guenbon.siso.dto.article.ArticleResponseDto;
import com.guenbon.siso.dto.comment.CommentResponseDto;
import com.guenbon.siso.dto.rating.RatingResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "회원 정보보기 폼 응답 dto")
public class MemberInfoResponse {
    @Schema(description = "작성 글 리스트")
    private List<ArticleResponseDto> articleResponseDtoList;
    @Schema(description = "작성 댓글 리스트")
    private List<CommentResponseDto> commentResponseDtoList;
    @Schema(description = "작성 평점 리스트")
    private List<RatingResponseDto> ratingResponseDtoList;
    @Schema(description = "프로필 조회 정보")
    private MemberInfoResponseDto memberInfoResponseDto;
}
