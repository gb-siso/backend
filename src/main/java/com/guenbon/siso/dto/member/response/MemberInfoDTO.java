package com.guenbon.siso.dto.member.response;

import com.guenbon.siso.dto.member.common.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "회원 정보보기 응답 dto")
public class MemberInfoDTO {
    @Schema(description = "작성 글 리스트")
    private List<ArticleDTO> articleList;
    @Schema(description = "작성 댓글 리스트")
    private List<CommentDTO> commentList;
    @Schema(description = "작성 평점 리스트")
    private List<RatingDTO> ratingList;
    @Schema(description = "프로필 조회 정보")
    private MemberDTO memberInfoResponseDto;

    @NoArgsConstructor
    @Setter
    @Getter
    @Schema(description = "회원 정보보기 응답 dto 내 작성글 dto")
    public static class ArticleDTO {
        private String id;
        private String title;
    }

    @NoArgsConstructor
    @Setter
    @Getter
    @Schema(description = "회원 정보보기 응답 dto 내 댓글 dto")
    public static class CommentDTO {
        private String id;
        private String content;
    }

    @NoArgsConstructor
    @Setter
    @Getter
    @Schema(description = "회원 정보보기 응답 dto 내 평점 dto")
    public static class RatingDTO {
        @Schema(description = "평점 id")
        private String id;
        @Schema(description = "평점 대상 id")
        private String targetId;
        @Schema(description = "평점 대상 이름")
        private String targetName;
        @Schema(description = "평점")
        private Float rate;
    }
}
