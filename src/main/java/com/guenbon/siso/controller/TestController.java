package com.guenbon.siso.controller;

import com.guenbon.siso.service.auth.JwtTokenProvider;
import com.guenbon.siso.service.member.MemberService;
import com.guenbon.siso.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestController {

    private final AESUtil aesUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    @GetMapping("/accessToken/{memberId}")
    public String accessToken(@PathVariable Long memberId) {
        return jwtTokenProvider.createAccessToken(memberService.findById(memberId));
    }

    @GetMapping("/encrypt/{value}")
    public String encrypt(@PathVariable Long value) {
        return aesUtil.encrypt(value);
    }
}
