package com.guenbon.siso.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.dto.rating.request.RatingWriteDTO;
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.exception.errorCode.MemberErrorCode;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Import({AESUtil.class, JwtTokenProvider.class})
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    AESUtil aesUtil;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    void 빈_주입_화인() {
        assertAll(
                () -> assertThat(mockMvc).isNotNull(),
                () -> assertThat(aesUtil).isNotNull(),
                () -> assertThat(jwtTokenProvider).isNotNull()
        );
    }

    @Test
    @DisplayName("이미 존재하는 Rating 작성 요청에 대해 에러 응답을 반환한다")
    void create_duplicate_ErrorResponse() throws Exception {

        final Long CONGRESSMAN_ID = 1L;
        final String ENCRYPTED_CONGRESSMAN_ID = aesUtil.encrypt(CONGRESSMAN_ID);
        final Long MEMBER_ID = 10L;
        final String accessToken = jwtTokenProvider.createAccessToken(MEMBER_ID);

        final RatingWriteDTO BAD_REQUEST = RatingWriteDTO.builder()
                .congressmanId(ENCRYPTED_CONGRESSMAN_ID)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        ObjectMapper objectMapper = new ObjectMapper();
        final String json = objectMapper.writeValueAsString(BAD_REQUEST);

        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(RatingErrorCode.DUPLICATED.getMessage()))
                .andExpect(jsonPath("$.code").value(RatingErrorCode.DUPLICATED.name()))
                .andReturn();
    }

    @Test
    @DisplayName("존재하지 않는 회원의 Rating 작성 요청에 대해 에러 응답을 반환한다")
    void create_notExistMember_ErrorResponse() throws Exception {
        final Long CONGRESSMAN_ID = 1L;
        final String ENCRYPTED_CONGRESSMAN_ID = aesUtil.encrypt(CONGRESSMAN_ID);
        final Long INVALID_MEMBER_ID = 10L;
        final String accessToken = jwtTokenProvider.createAccessToken(INVALID_MEMBER_ID);

        final RatingWriteDTO BAD_REQUEST = RatingWriteDTO.builder()
                .congressmanId(ENCRYPTED_CONGRESSMAN_ID)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        ObjectMapper objectMapper = new ObjectMapper();
        final String json = objectMapper.writeValueAsString(BAD_REQUEST);

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
    @DisplayName("존재하지 않는 국회의원의 Rating 작성 요청에 대해 에러 응답을 반환한다")
    void create_notExistCongressman_ErrorResponse() throws Exception {
        final Long INVALID_CONGRESSMAN_ID = 1L;
        final String ENCRYPTED_CONGRESSMAN_ID = aesUtil.encrypt(INVALID_CONGRESSMAN_ID);
        final Long MEMBER_ID = 10L;
        final String accessToken = jwtTokenProvider.createAccessToken(MEMBER_ID);

        final RatingWriteDTO BAD_REQUEST = RatingWriteDTO.builder()
                .congressmanId(ENCRYPTED_CONGRESSMAN_ID)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        ObjectMapper objectMapper = new ObjectMapper();
        final String json = objectMapper.writeValueAsString(BAD_REQUEST);

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
}