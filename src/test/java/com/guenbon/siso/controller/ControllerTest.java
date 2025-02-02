package com.guenbon.siso.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.service.auth.AuthService;
import com.guenbon.siso.service.auth.JwtTokenProvider;
import com.guenbon.siso.service.congressman.CongressmanApiService;
import com.guenbon.siso.service.congressman.CongressmanService;
import com.guenbon.siso.service.member.MemberService;
import com.guenbon.siso.service.rating.RatingService;
import com.guenbon.siso.util.AESUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Slf4j
public class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @MockitoBean
    protected AESUtil aesUtil;
    @MockitoBean
    protected CongressmanService congressmanService;
    @MockitoBean
    protected CongressmanApiService congressmanApiService;
    @MockitoBean
    protected JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    protected RatingService ratingService;
    @MockitoBean
    protected MemberService memberService;
    @MockitoBean
    protected AuthService authService;
    protected final ObjectMapper objectMapper = new ObjectMapper();
}
