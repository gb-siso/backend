package com.guenbon.siso.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.CongressmanService;
import com.guenbon.siso.service.JwtTokenProvider;
import com.guenbon.siso.service.MemberService;
import com.guenbon.siso.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@Slf4j
public class ControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockitoBean
    AESUtil aesUtil;
    @MockitoBean
    CongressmanService congressmanService;
    @MockitoBean
    JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    RatingService ratingService;
    @MockitoBean
    MemberService memberService;
    @Value("${spring.siso.domain}")
    String domain;
    protected final ObjectMapper objectMapper = new ObjectMapper();
}
