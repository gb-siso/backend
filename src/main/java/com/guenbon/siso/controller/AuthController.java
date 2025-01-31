package com.guenbon.siso.controller;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.dto.auth.kakao.KakaoToken;
import com.guenbon.siso.dto.auth.kakao.UserInfo;
import com.guenbon.siso.dto.auth.response.LoginDTO;
import com.guenbon.siso.service.AuthService;
import com.guenbon.siso.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;

    @GetMapping("/login/kakao")
    public ResponseEntity<LoginDTO> kakaoLogin(@RequestParam String code) {
        KakaoToken token = authService.getToken(code);
        UserInfo userInfo = authService.getUserInfo(token);
        IssueTokenResult issueTokenResult = memberService.issueToken(userInfo.getId());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.SET_COOKIE, issueTokenResult.getRefreshTokenCookie());
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(LoginDTO.from(issueTokenResult));
    }

    @PostMapping
    public ResponseEntity<LoginDTO> kakaoReissue(String refreshToken) {
        return null;
    }

    @DeleteMapping
    public ResponseEntity<Void> logOut(Long memberId) {
        return null;
    }
}
