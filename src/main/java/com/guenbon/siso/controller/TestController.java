package com.guenbon.siso.controller;

import com.guenbon.siso.service.AESUtil;
import com.guenbon.siso.service.CongressmanService;
import com.guenbon.siso.service.JwtTokenProvider;
import com.guenbon.siso.service.MemberService;
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
    private final CongressmanService congressmanService;

    @GetMapping("/accessToken/{memberId}")
    public String accessToken(@PathVariable Long memberId) {
        memberService.findById(memberId);
        return jwtTokenProvider.createAccessToken(memberId);
    }

    @GetMapping("/congressman/{congressmanId}")
    public String congressman(@PathVariable Long congressmanId) {
        congressmanService.findById(congressmanId);
        return aesUtil.encrypt(congressmanId);
    }
}
