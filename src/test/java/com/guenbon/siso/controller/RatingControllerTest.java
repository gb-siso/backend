package com.guenbon.siso.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(RatingController.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void mockMvc_null_아님() {
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("이미 존재하는 Rating 작성 요청에 대해 에러 응답을 반환한다.")
    void create_duplicate_ErrorResponse() throws Exception {
        final RatingWriteDTO BAD_REQUEST = RatingWriteDTO.builder()
                .congressmanId("123abcdef")
                .content("평범한 국회의원")
                .rating(3.0F).build();

        ObjectMapper objectMapper = new ObjectMapper();
        final String json = objectMapper.writeValueAsString(BAD_REQUEST);

        MvcResult mvcResult = mockMvc.perform(post("/hello/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value("중복되는 Rating 입니다"))
                .andReturn();
    }
}