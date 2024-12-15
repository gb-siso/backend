package com.guenbon.siso.dto.congressman.response;

import com.guenbon.siso.dto.congressman.common.CongressmanDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@Schema(description = "국회의원 상세보기 응답 dto")
public class CongressmanDetailDTO {
    private CongressmanDTO congressmanDTO;
    private List<News> newsList;
    private List<Bill> billList;
    private List<Rating> ratings;

    @Schema(description = "국회의원 뉴스 dto")
    public static class News {
        private String title;
        private String content;
        private String author;
    }

    @Schema(description = "국회의원 법안 dto, 요약 등은 추후 요약 ai api 확인하고 추가예정")
    public static class Bill {
        private String id;
        private String title;
        @Schema(description = "법안 상세페이지 링크")
        private String link;
    }

    @Schema(description = "국회의원 상세보기 내 평가 dto")
    public static class Rating {
        private String id;
        @Schema(description = "작성회원")
        private String MemberDTO;
        private LocalDateTime createdAt;
        @Schema(description = "평점")
        private Float rating;
        @Schema(description = "평가내용")
        private String content;
        @Schema(description = "좋아요 수")
        private Long likes;
        @Schema(description = "싫어요 수")
        private Long dislikes;
        @Schema(description = "좋아요 눌렀는지 여부")
        private Boolean liked;
        @Schema(description = "싫어요 눌렀는지 여부")
        private Boolean disliked;
    }
}
