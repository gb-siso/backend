package com.guenbon.siso.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.dto.congressman.common.CongressmanDTO;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.CongressmanService;
import com.guenbon.siso.service.JwtTokenProvider;
import com.guenbon.siso.support.fixture.congressman.CongressmanDTOFixture;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import({AESUtil.class, JwtTokenProvider.class})
@Slf4j
class CongressmanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AESUtil aesUtil;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    CongressmanService congressmanService;

    ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("GET /api/v1/congressman 요청 시 마지막 스크롤이 아닐 경우 정상 응답")
    @Test
    void list_notLastScroll_CongressmanListDTO() throws Exception {
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

        final String EXPECTED_CONGRESSMAN_LIST_JSON = objectMapper.writeValueAsString(congressmanDTOList);

        // when then
        mockMvc.perform(get("/api/v1/congressman?size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.congressmanList").value(EXPECTED_CONGRESSMAN_LIST_JSON))
                .andExpect(jsonPath("$.idCursor").value(idCursor))
                .andExpect(jsonPath("$.rateCursor").value(rateCursor))
                .andExpect(jsonPath("$.lastPage").value(false));
    }

    @DisplayName("GET /api/v1/congressman 요청 시 마지막 스크롤일 경우 정상 응답")
    @Test
    void list_lastScroll_CongressmanListDTO() throws Exception {
        // given
        final List<CongressmanDTO> congressmanDTOList = List.of(
                CongressmanDTOFixture.builder()
                        .setId("adjflkjdak123")
                        .setRate(3.5).build(),
                CongressmanDTOFixture.builder()
                        .setId("adajdl12kjdak123")
                        .setRate(3.0).build()
        );

        final String EXPECTED_CONGRESSMAN_LIST_JSON = objectMapper.writeValueAsString(congressmanDTOList);

        // when then
        mockMvc.perform(get("/api/v1/congressman?size=2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.congressmanList").value(EXPECTED_CONGRESSMAN_LIST_JSON))
                .andExpect(jsonPath("$.idCursor").isEmpty())
                .andExpect(jsonPath("$.rateCursor").isEmpty())
                .andExpect(jsonPath("$.lastPage").value(true));
    }
}