package com.guenbon.siso.controller;

import static com.guenbon.siso.exception.errorCode.RatingErrorCode.DUPLICATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import com.guenbon.siso.exception.errorCode.CongressmanErrorCode;
import com.guenbon.siso.exception.errorCode.MemberErrorCode;
import com.guenbon.siso.exception.errorCode.RatingErrorCode;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.CongressmanService;
import com.guenbon.siso.service.JwtTokenProvider;
import com.guenbon.siso.service.MemberService;
import com.guenbon.siso.service.RatingService;
import com.guenbon.siso.support.fixture.congressman.CongressmanFixture;
import com.guenbon.siso.support.fixture.member.MemberFixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

    @Test
    void 빈_주입_화인() {
        assertAll(
                () -> assertThat(mockMvc).isNotNull(),
                () -> assertThat(aesUtil).isNotNull(),
                () -> assertThat(jwtTokenProvider).isNotNull()
        );
    }

    @Test
    @DisplayName("중복 Rating 작성 요청에 대해 에러 응답을 반환한다")
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

        doThrow(new BadRequestException(RatingErrorCode.DUPLICATED)).when(ratingService)
                .create(MEMBER_ID, CONGRESSMAN_ID);

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

        doThrow(new BadRequestException(MemberErrorCode.NOT_EXISTS)).when(ratingService)
                .create(INVALID_MEMBER_ID, CONGRESSMAN_ID);

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

        doThrow(new BadRequestException(CongressmanErrorCode.NOT_EXISTS)).when(ratingService)
                .create(MEMBER_ID, INVALID_CONGRESSMAN_ID);

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
    @DisplayName("정상 Rating 작성 요청에 대해 리다이렉션한다")
    void create_normalInput_200() throws Exception {

        final Member member = MemberFixture.builder()
                .setId(10L)
                .setNickname("장몽이")
                .build();
        final Congressman congressman = CongressmanFixture.builder()
                .setId(1L)
                .setName("이준석")
                .build();

        final String ENCRYPTED_CONGRESSMAN_ID = aesUtil.encrypt(congressman.getId());
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());

        final RatingWriteDTO REQUEST = RatingWriteDTO.builder()
                .congressmanId(ENCRYPTED_CONGRESSMAN_ID)
                .content("평범한 국회의원")
                .rating(3.0F).build();

        ObjectMapper objectMapper = new ObjectMapper();
        final String json = objectMapper.writeValueAsString(REQUEST);

        doNothing().when(ratingService).create(member.getId(), congressman.getId());

        mockMvc.perform(post("/api/v1/ratings")
                        .header("accessToken", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(domain + "/api/v1/congressionman/" + ENCRYPTED_CONGRESSMAN_ID))
                .andReturn();

        verify(ratingService, times(1)).create(member.getId(), congressman.getId());
    }
}