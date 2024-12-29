package com.guenbon.siso.controller;

import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.CongressmanService;
import com.guenbon.siso.service.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
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
}