package com.guenbon.siso.argumentresolver;

import com.guenbon.siso.controller.RatingController;
import com.guenbon.siso.exception.errorCode.PageableErrorCode;
import com.guenbon.siso.service.auth.JwtTokenProvider;
import com.guenbon.siso.service.rating.RatingService;
import com.guenbon.siso.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RatingController.class)
@Slf4j
class CustomPageableArgumentResolverTest {

    @MockitoBean
    protected JwtTokenProvider jwtTokenProvider;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AESUtil aesUtil;
    @MockitoBean
    private RatingService ratingService;

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
                .andExpect(jsonPath("$.code").value(expectedErrorCode.getCode()))
                .andExpect(jsonPath("$.message").value(expectedErrorCode.getMessage()))
                .andReturn();
    }

    private static Stream<Arguments> provideInvalidPageableParameters() {
        return Stream.of(
                Arguments.of("0", "10", "topicality,DESC", PageableErrorCode.INVALID_PAGE),
                Arguments.of("1", "-5", "topicality,DESC", PageableErrorCode.INVALID_SIZE),
                Arguments.of("abc", "10", "topicality,DESC", PageableErrorCode.INVALID_FORMAT),
                Arguments.of("1", "10", "like,INVALID", PageableErrorCode.UNSUPPORTED_SORT_DIRECTION)
        );
    }
}