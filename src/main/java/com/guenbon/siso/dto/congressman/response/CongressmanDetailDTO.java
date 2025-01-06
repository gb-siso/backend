package com.guenbon.siso.dto.congressman.response;

import com.guenbon.siso.dto.congressman.projection.CongressmanGetListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import com.guenbon.siso.exception.InternalServerException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    @Schema(description = "국회의원 정보")
    private CongressmanDTO congressmanDTO;
    @Schema(description = "국회의원 뉴스 리스트")
    private List<News> newsList;
    @Schema(description = "국회의원 발의 법안 리스트")
    private List<Bill> billList;
    @Schema(description = "국회의원 평가 리스트")
    private List<Rating> ratings;

    @Schema(description = "국회의원 뉴스 dto")
    public static class News {
        @Schema(description = "뉴스 제목")
        private String title;
        @Schema(description = "뉴스 내용")
        private String content;
        @Schema(description = "뉴스 작성 기자 이름")
        private String author;
    }

    @Schema(description = "국회의원 법안 dto, 요약 등은 추후 요약 ai api 확인하고 추가예정")
    public static class Bill {
        @Schema(description = "법안 id")
        private String id;
        @Schema(description = "법안 명")
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
