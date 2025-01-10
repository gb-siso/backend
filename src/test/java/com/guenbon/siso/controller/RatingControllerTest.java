package com.guenbon.siso.controller;

import static com.guenbon.siso.exception.errorCode.RatingErrorCode.DUPLICATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
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
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.CongressmanService;
import com.guenbon.siso.service.JwtTokenProvider;
import com.guenbon.siso.service.MemberService;
import com.guenbon.siso.service.RatingService;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import({AESUtil.class, JwtTokenProvider.class})
@Slf4j
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    AESUtil aesUtil;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    RatingService ratingService;
    @MockitoBean
    CongressmanService congressmanService;
    @MockitoBean
    MemberService memberService;
    @Value("${spring.siso.domain}")
    String domain;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    void POST_api_v1_ratings_duplicateRequest_returnsErrorResponse() throws Exception {
        final Long congressmanId = 1L;
        final String encryptedCongressmanId = aesUtil.encrypt(congressmanId);
        final Long memberId = 10L;
        final String accessToken = jwtTokenProvider.createAccessToken(memberId);

        final RatingWriteDTO badRequest = RatingWriteDTO.builder()
                .congressmanId(encryptedCongressmanId)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        final String json = objectMapper.writeValueAsString(badRequest);

        doThrow(new BadRequestException(RatingErrorCode.DUPLICATED)).when(ratingService)
                .create(memberId, congressmanId);

        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", accessToken)
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
    void POST_api_v1_ratings_memberNotExists_returnsErrorResponse() throws Exception {
        final Long congressmanId = 1L;
        final String encryptedCongressmanId = aesUtil.encrypt(congressmanId);
        final Long invalidMemberId = 10L;
        final String accessToken = jwtTokenProvider.createAccessToken(invalidMemberId);

        final RatingWriteDTO badRequest = RatingWriteDTO.builder()
                .congressmanId(encryptedCongressmanId)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        final String json = objectMapper.writeValueAsString(badRequest);

        doThrow(new BadRequestException(MemberErrorCode.NOT_EXISTS)).when(ratingService)
                .create(invalidMemberId, congressmanId);

        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", accessToken)
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
    void POST_api_v1_ratings_congressmanNotExists_returnsErrorResponse() throws Exception {
        final Long invalidCongressmanId = 1L;
        final String encryptedCongressmanId = aesUtil.encrypt(invalidCongressmanId);
        final Long memberId = 10L;
        final String accessToken = jwtTokenProvider.createAccessToken(memberId);

        final RatingWriteDTO badRequest = RatingWriteDTO.builder()
                .congressmanId(encryptedCongressmanId)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        final String json = objectMapper.writeValueAsString(badRequest);

        doThrow(new BadRequestException(CongressmanErrorCode.NOT_EXISTS)).when(ratingService)
                .create(memberId, invalidCongressmanId);

        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(CongressmanErrorCode.NOT_EXISTS.getMessage()))
                .andExpect(jsonPath("$.code").value(CongressmanErrorCode.NOT_EXISTS.name()))
                .andReturn();
    }

    @Test
    @DisplayName("정상적인 Rating 작성 요청 시 리다이렉트 응답 반환")
    void POST_api_v1_ratings_validRequest_performsRedirection() throws Exception {
        final Member member = MemberFixture.builder()
                .setId(10L)
                .setNickname("장몽이")
                .build();
        final Congressman congressman = CongressmanFixture.builder()
                .setId(1L)
                .setName("이준석")
                .build();

        final String encryptedCongressmanId = aesUtil.encrypt(congressman.getId());
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        final RatingWriteDTO request = RatingWriteDTO.builder()
                .congressmanId(encryptedCongressmanId)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        final String json = objectMapper.writeValueAsString(request);

        doNothing().when(ratingService).create(member.getId(), congressman.getId());

        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(domain + "/api/v1/congressionman/" + encryptedCongressmanId))
                .andReturn();

        verify(ratingService, times(1)).create(member.getId(), congressman.getId());
    }

    @ParameterizedTest(name = "idCursor={0}, countCursor={1}일 때, 에러 코드={2} 반환")
    @MethodSource("provideInvalidCountCursorParameters")
    @DisplayName("유효하지 않은 커서 값 요청 시 에러 응답 반환")
    void GET_api_v1_ratings_invalidCursorValues_returnsValidationErrorResponse(
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
                Arguments.of("", "10", CursorErrorCode.NULL_OR_EMPTY_VALUE),
                Arguments.of("abc123def456", "", CursorErrorCode.NULL_OR_EMPTY_VALUE),
                Arguments.of("validIdCursor", "-1", CursorErrorCode.NEGATIVE_VALUE),
                Arguments.of("abc123def456", "abcdef", CommonErrorCode.TYPE_MISMATCH)
        );
    }

    @ParameterizedTest(name = "page={0}, size={1}, sort={2}일 때, 에러 코드={3} 반환")
    @MethodSource("provideInvalidPageableParameters")
    @DisplayName("유효하지 않은 Pageable 파라미터 요청 시 에러 응답 반환")
    void GET_api_v1_ratings_invalidPageableFields_returnsValidationErrorResponse(
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
}
