package com.guenbon.siso.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import com.guenbon.siso.dto.news.NewsDTO;
import com.guenbon.siso.dto.news.NewsListDTO;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.AESErrorCode;
import com.guenbon.siso.exception.errorCode.ApiErrorCode;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.exception.errorCode.ErrorCode;
import com.guenbon.siso.support.fixture.congressman.CongressmanDTOFixture;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.MediaType;

@WebMvcTest
@Slf4j
class CongressmanControllerTest extends ControllerTest {

    public static final String BASE_URL = "/api/v1/congressman";

    @DisplayName("GET:" + BASE_URL + " 성공적으로 Congressman 목록을 반환한다")
    @Test
    void congressmanList_validRequest_ReturnsCongressmanList() throws Exception {
        // given
        final String idCursor = "addafjl6102lkjdak123";
        final Double rateCursor = 2.5;

        final List<CongressmanDTO> congressmanDTOList = List.of(
                CongressmanDTOFixture.builder()
                        .setId("adjflkjdak123")
                        .setRate(3.5).build(),
                CongressmanDTOFixture.builder()
                        .setId("adajdl12kjdak123")
                        .setRate(3.0).build(),
                CongressmanDTOFixture.builder()
                        .setId(idCursor)
                        .setRate(rateCursor).build()
        );

        final CongressmanListDTO congressmanListDTO = CongressmanListDTO.builder()
                .congressmanList(congressmanDTOList).idCursor(idCursor).rateCursor(rateCursor).lastPage(false).build();
        final String ENCRYPTED_LONG_MAX = "qkqh123qkqh456";

        when(aesUtil.encrypt(Long.MAX_VALUE)).thenReturn(ENCRYPTED_LONG_MAX);
        when(congressmanService.getCongressmanListDTO(
                PageRequest.of(0, 2, Sort.by(Sort.Order.desc("rate"))),
                ENCRYPTED_LONG_MAX, null, null, null)).thenReturn(congressmanListDTO);

        // when then
        mockMvc.perform(get(BASE_URL + "?size=2").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.congressmanList[0].id").value("adjflkjdak123"))
                .andExpect(jsonPath("$.congressmanList[1].id").value("adajdl12kjdak123"))
                .andExpect(jsonPath("$.congressmanList[2].id").value(idCursor))
                .andExpect(jsonPath("$.idCursor").value(idCursor))
                .andExpect(jsonPath("$.rateCursor").value(rateCursor))
                .andExpect(jsonPath("$.lastPage").value(false));
    }

    @DisplayName("GET:" + BASE_URL + " 마지막 페이지 정보를 포함하여 Congressman 목록을 반환한다")
    @Test
    void congressmanList_validRequest_ReturnsCongressmanListWithLastPageInfo() throws Exception {
        // given
        final List<CongressmanDTO> congressmanDTOList = List.of(
                CongressmanDTOFixture.builder()
                        .setId("adjflkjdak123")
                        .setRate(3.5).build(),
                CongressmanDTOFixture.builder()
                        .setId("adajdl12kjdak123")
                        .setRate(3.0).build()
        );

        final CongressmanListDTO congressmanListDTO = CongressmanListDTO.builder().congressmanList(congressmanDTOList)
                .lastPage(true).build();

        final String ENCRYPTED_LONG_MAX = "qkqh123qkqh456";

        when(aesUtil.encrypt(Long.MAX_VALUE)).thenReturn("qkqh123qkqh456");
        when(congressmanService.getCongressmanListDTO(
                PageRequest.of(0, 2, Sort.by(Sort.Order.desc("rate"))),
                ENCRYPTED_LONG_MAX, null, null, null)).thenReturn(congressmanListDTO);

        // when then
        mockMvc.perform(get(BASE_URL + "?size=2").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.congressmanList[0].id").value("adjflkjdak123"))
                .andExpect(jsonPath("$.congressmanList[1].id").value("adajdl12kjdak123"))
                .andExpect(jsonPath("$.idCursor").isEmpty())
                .andExpect(jsonPath("$.rateCursor").isEmpty())
                .andExpect(jsonPath("$.lastPage").value(true));
    }

    @DisplayName("GET:" + BASE_URL + " 유효하지 않은 sort 파라미터로 요청 시 예외를 응답한다")
    @Test
    void congressmanList_invalidSortParameter_ReturnsBadRequest() throws Exception {
        // when then
        mockMvc.perform(get(BASE_URL + "?size=2&sort=invalid,DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("지원하지 않는 정렬 필드입니다."))
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_SORT_PROPERTY"));
    }

    @DisplayName("GET:/api/v1/news/{congressmanId} 유효하지 않은 congressmanId 요청 시 예외를 응답한다")
    @ParameterizedTest(name = "congressmanId : {0} 요청 시 {1} 에러코드 응답 반환")
    @MethodSource("provideInvalidCongressmanIdScenarios")
    void newsList_invalidCongressmanId_ReturnsBadRequest(String decryptedCongressmanId, ErrorCode expectedCode)
            throws Exception {
        // given
        when(congressmanService.findNewsList(decryptedCongressmanId,
                PageRequest.of(0, 10, Sort.by(Sort.Order.desc("regDate"))))).thenThrow(
                new BadRequestException(expectedCode));

        // when, then
        mockMvc.perform(
                        get(BASE_URL + "/news/" + decryptedCongressmanId).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(expectedCode.getMessage()))
                .andExpect(jsonPath("$.code").value(expectedCode.name()));
    }

    private static Stream<Arguments> provideInvalidCongressmanIdScenarios() {
        return Stream.of(
                Arguments.of("invalidCongressmanId", AESErrorCode.INVALID_INPUT),
                Arguments.of("notExistCongressmanId", CongressmanErrorCode.NOT_EXISTS)
        );
    }

    @DisplayName("GET:/api/v1/news/{congressmanId} 에서 ApiErrorCode에 따른 API 예외를 응답한다")
    @ParameterizedTest
    @EnumSource(ApiErrorCode.class)
    void newsList_ApiErrorCode_ReturnsApiErrorResponse(ApiErrorCode apiErrorCode) throws Exception {
        // given
        final String decryptedCongressmanId = "decryptedCongressmanId";
        when(congressmanService.findNewsList(decryptedCongressmanId,
                PageRequest.of(0, 10, Sort.by(Sort.Order.desc("regDate"))))).thenThrow(
                new ApiException(apiErrorCode));

        // when, then
        mockMvc.perform(
                        get(BASE_URL + "/news/" + decryptedCongressmanId).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(apiErrorCode.getMessage()))
                .andExpect(jsonPath("$.code").value(apiErrorCode.name()));
    }

    @DisplayName("GET:/api/v1/news/{congressmanId} 에 유효한 congressmanId 요청 시 뉴스 목록을 응답한다")
    @Test
    void newsList_validCongressmanId_ReturnsNewsList() throws Exception {
        // given
        final String decryptedCongressmanId = "decryptedCongressmanId";

        final NewsDTO news1 = NewsDTO.of("기사제목1", "link1", "2025-01-21 09:00");
        final NewsDTO news2 = NewsDTO.of("기사제목2", "link2", "2025-01-15 08:00");
        final PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Order.desc("regDate")));

        when(congressmanService.findNewsList(decryptedCongressmanId,
                pageRequest)).thenReturn(
                NewsListDTO.of(List.of(news1, news2), 10)
        );
        // when, then
        // when, then
        mockMvc.perform(
                        get(BASE_URL + "/news/" + decryptedCongressmanId).contentType(MediaType.APPLICATION_JSON)
                                .param("size", "2"))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.lastPage").value(10))
                .andExpect(jsonPath("$.newsList[0].title").value("기사제목1"))
                .andExpect(jsonPath("$.newsList[1].title").value("기사제목2"));

        verify(congressmanService, times(1)).findNewsList(decryptedCongressmanId, pageRequest);
    }
}