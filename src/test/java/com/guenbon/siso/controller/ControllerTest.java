package com.guenbon.siso.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.AuthService;
import com.guenbon.siso.service.CongressmanService;
import com.guenbon.siso.service.JwtTokenProvider;
import com.guenbon.siso.service.MemberService;
import com.guenbon.siso.service.RatingService;
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
    protected JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    protected RatingService ratingService;
    @MockitoBean
    protected MemberService memberService;
    @MockitoBean
    protected AuthService authService;
    protected final ObjectMapper objectMapper = new ObjectMapper();
}
