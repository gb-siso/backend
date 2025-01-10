package com.guenbon.siso.controller;

import static com.guenbon.siso.exception.errorCode.RatingErrorCode.DUPLICATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.guenbon.siso.dto.cursor.count.CountCursor;
import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import com.guenbon.siso.dto.rating.response.RatingDetailDTO;
import com.guenbon.siso.dto.rating.response.RatingListDTO;
import com.guenbon.siso.entity.Congressman;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.CommonErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.exception.errorCode.CursorErrorCode;
import com.guenbon.siso.exception.errorCode.ErrorCode;
import com.guenbon.siso.exception.errorCode.MemberErrorCode;
import com.guenbon.siso.exception.errorCode.PageableErrorCode;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import com.guenbon.siso.support.fixture.rating.RatingDetailDTOFixture;
import com.guenbon.siso.support.fixture.rating.RatingWriteDTOFixture;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

class RatingControllerTest extends ControllerTest {

    public static final String ENCRYPTED_CONGRESSMAN_ID = "encryptedCongressmanId";
    public static final Long CONGRESSMAN_ID = 1L;
    public static final Long MEMBER_ID = 10L;
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String BLANK_STRING = "";

    @Test
    @DisplayName("빈 주입 확인 - MockMvc, AESUtil, JwtTokenProvider, ObjectMapper 빈 정상 주입")
    void allBeansInjectedSuccessfully() {
        assertAll(
                () -> assertThat(mockMvc).isNotNull(),
                () -> assertThat(aesUtil).isNotNull(),
                () -> assertThat(jwtTokenProvider).isNotNull(),
                () -> assertThat(objectMapper).isNotNull()
        );
    }

    @Test
    @DisplayName("중복된 Rating 작성 요청 시 에러 응답 반환")
    void ratingSave_ratings_duplicateRequest_returnsErrorResponse() throws Exception {
        final RatingWriteDTO badRequest = RatingWriteDTO.builder()
                .congressmanId(ENCRYPTED_CONGRESSMAN_ID)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        final String json = objectMapper.writeValueAsString(badRequest);

        when(aesUtil.decrypt(ENCRYPTED_CONGRESSMAN_ID)).thenReturn(CONGRESSMAN_ID);
        when(jwtTokenProvider.getMemberId(ACCESS_TOKEN)).thenReturn(MEMBER_ID);

        doThrow(new BadRequestException(RatingErrorCode.DUPLICATED)).when(ratingService)
                .create(MEMBER_ID, CONGRESSMAN_ID);

        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(DUPLICATED.getMessage()))
                .andExpect(jsonPath("$.code").value(DUPLICATED.name()))
                .andReturn();
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 Rating 작성 요청 시 에러 응답 반환")
    void ratingSave_ratings_memberNotExists_returnsErrorResponse() throws Exception {
        final RatingWriteDTO badRequest = RatingWriteDTO.builder()
                .congressmanId(ENCRYPTED_CONGRESSMAN_ID)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        final String json = objectMapper.writeValueAsString(badRequest);

        when(aesUtil.decrypt(ENCRYPTED_CONGRESSMAN_ID)).thenReturn(CONGRESSMAN_ID);
        when(jwtTokenProvider.getMemberId(ACCESS_TOKEN)).thenReturn(MEMBER_ID);

        doThrow(new BadRequestException(MemberErrorCode.NOT_EXISTS)).when(ratingService)
                .create(MEMBER_ID, CONGRESSMAN_ID);

        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(MemberErrorCode.NOT_EXISTS.getMessage()))
                .andExpect(jsonPath("$.code").value(MemberErrorCode.NOT_EXISTS.name()))
                .andReturn();
    }

    @Test
    @DisplayName("존재하지 않는 국회의원으로 Rating 작성 요청 시 에러 응답 반환")
    void ratingSave_ratings_congressmanNotExists_returnsErrorResponse() throws Exception {
        final RatingWriteDTO badRequest = RatingWriteDTO.builder()
                .congressmanId(ENCRYPTED_CONGRESSMAN_ID)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        final String json = objectMapper.writeValueAsString(badRequest);

        when(aesUtil.decrypt(ENCRYPTED_CONGRESSMAN_ID)).thenReturn(CONGRESSMAN_ID);
        when(jwtTokenProvider.getMemberId(ACCESS_TOKEN)).thenReturn(MEMBER_ID);

        doThrow(new BadRequestException(CongressmanErrorCode.NOT_EXISTS)).when(ratingService)
                .create(MEMBER_ID, CONGRESSMAN_ID);

        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(CongressmanErrorCode.NOT_EXISTS.getMessage()))
                .andExpect(jsonPath("$.code").value(CongressmanErrorCode.NOT_EXISTS.name()))
                .andReturn();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidRatingWriteDTO")
    @DisplayName("유효하지 않은 요청 바디 값으로 Rating 작성 요청 시 에러 응답 반환")
    void ratingSave_invalidRequestBody_returnsErrorResponse(final RatingWriteDTO invalidRatingWriteDTO,
                                                            String errorField, String errorMessage) throws Exception {
        // given, when
        final String json = objectMapper.writeValueAsString(invalidRatingWriteDTO);
        when(jwtTokenProvider.getMemberId(ACCESS_TOKEN)).thenReturn(MEMBER_ID);

        // then
        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", ACCESS_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest()) // HTTP 상태코드 400 검증
                .andExpect(jsonPath("$.message").value("유효하지 않은 입력 값입니다")) // 오류 메시지 검증
                .andExpect(jsonPath("$.code").value("INVALID_INPUT_VALUE")) // 오류 코드 검증
                .andExpect(jsonPath("$.errors[0].field").value(errorField)) // 에러 필드 검증
                .andExpect(jsonPath("$.errors[0].message").value(errorMessage)); // 필드 관련 메시지 검증
    }

    static Stream<Arguments> provideInvalidRatingWriteDTO() {
        return Stream.of(
                Arguments.of(Named.named("blank congressmanId",
                                RatingWriteDTOFixture.builder()
                                        .setCongressmanId(BLANK_STRING).build()),
                        "congressmanId", "congressmanId는 필수입니다."),
                Arguments.of(Named.named("blank content",
                                RatingWriteDTOFixture.builder()
                                        .setContent(BLANK_STRING).build()),
                        "content", "content는 필수입니다."),
                Arguments.of(Named.named("100자 넘는 content",
                                RatingWriteDTOFixture.builder()
                                        .setContent(new StringBuilder()
                                                .append("a".repeat(101)) // 101자 길이 문자열 생성
                                                .toString()).build()),
                        "content", "content는 100자 이하여야 합니다."),
                Arguments.of(Named.named("null rating",
                                RatingWriteDTOFixture.builder()
                                        .setRating(null).build()),
                        "rating", "rating는 필수입니다."),
                Arguments.of(Named.named("rating below minimum",
                                RatingWriteDTOFixture.builder()
                                        .setRating(-1.0f).build()),
                        "rating", "rating은 0.0 이상이어야 합니다."),
                Arguments.of(Named.named("rating above maximum",
                                RatingWriteDTOFixture.builder()
                                        .setRating(6.0f).build()),
                        "rating", "rating은 5.0 이하여야 합니다."),
                Arguments.of(Named.named("rating with more than one decimal place",
                                RatingWriteDTOFixture.builder()
                                        .setRating(3.123f).build()),
                        "rating", "rating은 소수점 1자리까지 입력 가능합니다.")
        );
    }


    @Test
    @DisplayName("정상적인 Rating 작성 요청 시 리다이렉트 응답 반환")
    void ratingSave_ratings_validRequest_performsRedirection() throws Exception {
        final Member member = MemberFixture.builder()
                .setId(MEMBER_ID)
                .setNickname("장몽이")
                .build();
        final Congressman congressman = CongressmanFixture.builder()
                .setId(CONGRESSMAN_ID)
                .setName("이준석")
                .build();

        final String encryptedCongressmanId = "encryptedCongressmanId";
        final String accessToken = "accessToken";

        final RatingWriteDTO request = RatingWriteDTO.builder()
                .congressmanId(encryptedCongressmanId)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        final String json = objectMapper.writeValueAsString(request);

        when(aesUtil.decrypt(encryptedCongressmanId)).thenReturn(CONGRESSMAN_ID);
        when(jwtTokenProvider.getMemberId(accessToken)).thenReturn(MEMBER_ID);

        doNothing().when(ratingService).create(member.getId(), congressman.getId());

        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(domain + "/api/v1/congressman/" + encryptedCongressmanId))
                .andReturn();

        verify(ratingService, times(1)).create(member.getId(), congressman.getId());
    }

    @ParameterizedTest(name = "idCursor={0}, countCursor={1}일 때, 에러 코드={2} 반환")
    @MethodSource("provideInvalidCountCursorParameters")
    @DisplayName("국회의원 평가 목록 api에 유효하지 않은 커서 값 요청 시 에러 응답 반환")
    void ratingList_invalidCursorValues_returnsValidationErrorResponse(
            String idCursor,
            String countCursor,
            ErrorCode expectedErrorCode) throws Exception {
        mockMvc.perform(get("/api/v1/ratings/{encryptedCongressmanId}", "encryptedCongressmanId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("idCursor", idCursor)
                        .param("countCursor", countCursor))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(expectedErrorCode.name()))
                .andExpect(jsonPath("$.message").value(expectedErrorCode.getMessage()))
                .andReturn();
    }

    private static Stream<Arguments> provideInvalidCountCursorParameters() {
        return Stream.of(
                Arguments.of(BLANK_STRING, "10", CursorErrorCode.NULL_OR_EMPTY_VALUE),
                Arguments.of("abc123def456", BLANK_STRING, CursorErrorCode.NULL_OR_EMPTY_VALUE),
                Arguments.of("validIdCursor", "-1", CursorErrorCode.NEGATIVE_VALUE),
                Arguments.of("abc123def456", "abcdef", CommonErrorCode.TYPE_MISMATCH)
        );
    }

    @ParameterizedTest(name = "page={0}, size={1}, sort={2}일 때, 에러 코드={3} 반환")
    @MethodSource("provideInvalidPageableParameters")
    @DisplayName("국회의원 평가 목록 api에 유효하지 않은 Pageable 파라미터 요청 시 에러 응답 반환")
    void ratingList_invalidPageableFields_returnsValidationErrorResponse(
            String page,
            String size,
            String sort,
            PageableErrorCode expectedErrorCode) throws Exception {
        mockMvc.perform(get("/api/v1/ratings/{encryptedCongressmanId}", "encryptedCongressmanId")
                        .param("page", page)
                        .param("size", size)
                        .param("sort", sort)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(expectedErrorCode.name()))
                .andExpect(jsonPath("$.message").value(expectedErrorCode.getMessage()))
                .andReturn();
    }

    private static Stream<Arguments> provideInvalidPageableParameters() {
        return Stream.of(
                Arguments.of("-1", "10", "topicality,DESC", PageableErrorCode.INVALID_PAGE),
                Arguments.of("0", "-5", "topicality,DESC", PageableErrorCode.INVALID_SIZE),
                Arguments.of("abc", "10", "topicality,DESC", PageableErrorCode.INVALID_FORMAT),
                Arguments.of("0", "10", "invalidField,DESC", PageableErrorCode.UNSUPPORTED_SORT_PROPERTY),
                Arguments.of("0", "10", "like,INVALID", PageableErrorCode.UNSUPPORTED_SORT_DIRECTION),
                Arguments.of("0", "10", "like,", PageableErrorCode.UNSUPPORTED_SORT_DIRECTION)
        );
    }

    @DisplayName("국회의원 평가 목록 api에 유효한 파라미터 요청 시 RatingList 반환")
    @Test
    void ratingList_validRequest_returnsRatingList() throws Exception {
        // given
        final String size = "2";

        final List<RatingDetailDTO> ratingDetailDTOList = List.of(
                RatingDetailDTOFixture.builder().setId("1").build(),
                RatingDetailDTOFixture.builder().setId("2").build(),
                RatingDetailDTOFixture.builder().setId("3").build()
        );

        final CountCursor countCursor = CountCursor.of("3", 12);

        when(ratingService.validateAndGetRecentRatings(ENCRYPTED_CONGRESSMAN_ID,
                PageRequest.of(0, 2, Sort.by("topicality").descending()), null))
                .thenReturn(RatingListDTO.of(ratingDetailDTOList, countCursor));

        // when, then
        mockMvc.perform(get("/api/v1/ratings/{encryptedCongressmanId}", "encryptedCongressmanId")
                        .param("size", size)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.ratingList[0].id").value("1"))
                .andExpect(jsonPath("$.ratingList[1].id").value("2"))
                .andExpect(jsonPath("$.ratingList[2].id").value("3"))
                .andExpect(jsonPath("$.countCursor.idCursor").value("3"))
                .andExpect(jsonPath("$.countCursor.countCursor").value(12));

        verify(ratingService, times(1)).validateAndGetRecentRatings(ENCRYPTED_CONGRESSMAN_ID,
                PageRequest.of(0, 2, Sort.by("topicality").descending()), null);
    }
}
