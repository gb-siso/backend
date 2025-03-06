package com.guenbon.siso.controller;

import com.guenbon.siso.dto.reaction.response.RatingReactionDTO;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.reaction.RatingLikeErrorCode;
import com.guenbon.siso.service.auth.JwtTokenProvider;
import com.guenbon.siso.service.reaction.RatingLikeService;
import com.guenbon.siso.support.constants.ReactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReactionController.class)
@Slf4j
class ReactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    protected JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    protected RatingLikeService ratingLikeService;

    @Test
    @DisplayName("빈 주입 확인")
    void allBeansInjectedSuccessfully() {
        assertAll(
                () -> assertThat(mockMvc).isNotNull(),
                () -> assertThat(jwtTokenProvider).isNotNull(),
                () -> assertThat(ratingLikeService).isNotNull()
        );
    }

    @Test
    @DisplayName("이미 해당 평가에 대해 좋아요를 누른 회원이 평가 좋아요 작성 요청할 경우 좋아요 중복 예외응답을 한다.")
    void duplicated_createRatingLike_duplicatedRatingLikeErrorCode() throws Exception {
        // given
        final String ACCESS_TOKEN = "accessToken";
        final Long memberId = 1L;
        final String encryptedRatingId = "encryptedRatingId";

        when(jwtTokenProvider.getMemberId(ACCESS_TOKEN)).thenReturn(memberId);
        when(ratingLikeService.create(encryptedRatingId, memberId))
                .thenThrow(new CustomException(RatingLikeErrorCode.DUPLICATED));

        // when, then
        mockMvc.perform(post("/api/v1/likes/rating/" + encryptedRatingId)
                        .header("accessToken", ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(RatingLikeErrorCode.DUPLICATED.getMessage()))
                .andExpect(jsonPath("$.code").value(RatingLikeErrorCode.DUPLICATED.getCode()))
                .andReturn();

        verify(jwtTokenProvider, times(1)).getMemberId(ACCESS_TOKEN);
        verify(ratingLikeService, times(1)).create(encryptedRatingId, memberId);
    }

    @Test
    @DisplayName("싫어요를 누른 평가에 대해 좋아요 작성 요청하면 싫어요를 해제하고 좋아요를 작성한다. 이 때 응답은 싫어요가 삭제되었다는 내용을 포함한다.")
    void disliked_createRatingLike_deleteDislikeAndCreateLike() throws Exception {
        // given
        final String ACCESS_TOKEN = "accessToken";
        final Long memberId = 1L;
        final String encryptedRatingId = "encryptedRatingId";

        final RatingReactionDTO expected = RatingReactionDTO.of(encryptedRatingId,
                RatingReactionDTO.Reaction.of("likeId", ReactionStatus.CREATED),
                RatingReactionDTO.Reaction.of("dislikeId", ReactionStatus.DELETED)
        );

        when(jwtTokenProvider.getMemberId(ACCESS_TOKEN)).thenReturn(memberId);
        when(ratingLikeService.create(encryptedRatingId, memberId)).thenReturn(expected);

        // when, then
        mockMvc.perform(post("/api/v1/likes/rating/" + encryptedRatingId)
                        .header("accessToken", ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.like.status").value(ReactionStatus.CREATED.name()))
                .andExpect(jsonPath("$.dislike.status").value(ReactionStatus.DELETED.name()))
                .andReturn();

        verify(jwtTokenProvider, times(1)).getMemberId(ACCESS_TOKEN);
        verify(ratingLikeService, times(1)).create(encryptedRatingId, memberId);
    }

    @Test
    @DisplayName("평가에 대해 좋아요 작성 요청하면 좋아요를 작성하고 응답한다. 응답은 좋아요 생성 내용을 포함한다.")
    void valid_createRatingLike_createLike() throws Exception {
        // given
        final String ACCESS_TOKEN = "accessToken";
        final Long memberId = 1L;
        final String encryptedRatingId = "encryptedRatingId";

        final RatingReactionDTO expected = RatingReactionDTO.of(encryptedRatingId,
                RatingReactionDTO.Reaction.of("likeId", ReactionStatus.CREATED),
                RatingReactionDTO.Reaction.of(null, ReactionStatus.NONE)
        );

        when(jwtTokenProvider.getMemberId(ACCESS_TOKEN)).thenReturn(memberId);
        when(ratingLikeService.create(encryptedRatingId, memberId)).thenReturn(expected);

        // when, then
        mockMvc.perform(post("/api/v1/likes/rating/" + encryptedRatingId)
                        .header("accessToken", ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.like.status").value(ReactionStatus.CREATED.name()))
                .andExpect(jsonPath("$.dislike.status").value(ReactionStatus.NONE.name()))
                .andExpect(jsonPath("$.dislike.reactionId").value(Matchers.nullValue()))
                .andReturn();

        verify(jwtTokenProvider, times(1)).getMemberId(ACCESS_TOKEN);
        verify(ratingLikeService, times(1)).create(encryptedRatingId, memberId);
    }

    @Test
    @DisplayName("해당 평가에 대해 좋아요를 누르지 않은 회원이 평가 좋아요 해제 작성 요청할 경우 좋아요 누르지 않음 예외응답을 한다.")
    void notLiked_deleteRatingLike_notLikedErrorCode() throws Exception {
        // given
        final String ACCESS_TOKEN = "accessToken";
        final Long memberId = 1L;
        final String encryptedRatingId = "encryptedRatingId";

        when(jwtTokenProvider.getMemberId(ACCESS_TOKEN)).thenReturn(memberId);
        when(ratingLikeService.create(encryptedRatingId, memberId))
                .thenThrow(new CustomException(RatingLikeErrorCode.NOT_LIKED));

        // when, then
        mockMvc.perform(delete("/api/v1/likes/rating/" + encryptedRatingId)
                        .header("accessToken", ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(RatingLikeErrorCode.NOT_LIKED.getMessage()))
                .andExpect(jsonPath("$.code").value(RatingLikeErrorCode.NOT_LIKED.getCode()))
                .andReturn();

        verify(jwtTokenProvider, times(1)).getMemberId(ACCESS_TOKEN);
        verify(ratingLikeService, times(1)).create(encryptedRatingId, memberId);
    }

    @Test
    @DisplayName("본인이 누른 게 아닌 좋아요 해제 작성 요청할 경우 나의 좋아요가 아님 예외응답을 한다.")
    void notMyLike_deleteRatingLike_notMyLikeErrorCode() throws Exception {
        // given
        final String ACCESS_TOKEN = "accessToken";
        final Long memberId = 1L;
        final String encryptedRatingId = "encryptedRatingId";

        when(jwtTokenProvider.getMemberId(ACCESS_TOKEN)).thenReturn(memberId);
        when(ratingLikeService.create(encryptedRatingId, memberId))
                .thenThrow(new CustomException(RatingLikeErrorCode.NOT_MY_LIKE));

        // when, then
        mockMvc.perform(delete("/api/v1/likes/rating/" + encryptedRatingId)
                        .header("accessToken", ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(RatingLikeErrorCode.NOT_MY_LIKE.getMessage()))
                .andExpect(jsonPath("$.code").value(RatingLikeErrorCode.NOT_MY_LIKE.getCode()))
                .andReturn();

        verify(jwtTokenProvider, times(1)).getMemberId(ACCESS_TOKEN);
        verify(ratingLikeService, times(1)).create(encryptedRatingId, memberId);
    }
}