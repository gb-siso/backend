package com.guenbon.siso.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.guenbon.siso.dto.congressman.response.CongressmanListDTO;
import com.guenbon.siso.dto.congressman.response.CongressmanListDTO.CongressmanDTO;
import com.guenbon.siso.support.fixture.congressman.CongressmanDTOFixture;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

@WebMvcTest
@Slf4j
class CongressmanControllerTest extends ControllerTest {

    @DisplayName("GET:/api/v1/congressman 성공적으로 Congressman 목록을 반환한다")
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
        mockMvc.perform(get("/api/v1/congressman?size=2").contentType(MediaType.APPLICATION_JSON))
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

    @DisplayName("GET:/api/v1/congressman 마지막 페이지 정보를 포함하여 Congressman 목록을 반환한다")
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
        mockMvc.perform(get("/api/v1/congressman?size=2").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.congressmanList[0].id").value("adjflkjdak123"))
                .andExpect(jsonPath("$.congressmanList[1].id").value("adajdl12kjdak123"))
                .andExpect(jsonPath("$.idCursor").isEmpty())
                .andExpect(jsonPath("$.rateCursor").isEmpty())
                .andExpect(jsonPath("$.lastPage").value(true));
    }

    @DisplayName("GET:/api/v1/congressman 유효하지 않은 sort 파라미터로 요청 시 예외를 응답한다")
    @Test
    void congressmanList_invalidSortParameter_ReturnsBadRequest() throws Exception {
        // when then
        mockMvc.perform(get("/api/v1/congressman?size=2&sort=invalid,DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("지원하지 않는 정렬 필드입니다."))
                .andExpect(jsonPath("$.code").value("UNSUPPORTED_SORT_PROPERTY"));
    }
}