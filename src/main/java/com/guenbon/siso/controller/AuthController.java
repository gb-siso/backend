package com.guenbon.siso.controller;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.dto.auth.response.LoginDTO;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.KakaoApiErrorCode;
import com.guenbon.siso.service.auth.AuthApiService;
import com.guenbon.siso.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
    private final AuthApiService authApiService;

    @GetMapping("/login/kakao")
    public ResponseEntity<LoginDTO> kakaoLogin(@RequestParam(required = false) String code,
                                               @RequestParam(required = false) String error,
                                               @RequestParam(required = false, name = "error_description") String errorDescription) {
        handleError(error);
        final IssueTokenResult issueTokenResult = authApiService.authenticateWithKakao(code);
        return ResponseEntity.ok()
                .headers(h -> h.add(HttpHeaders.SET_COOKIE, issueTokenResult.getRefreshTokenCookie()))
                .body(LoginDTO.from(issueTokenResult));
    }

    private static void handleError(String error) {
        if (error != null) {
            throw new CustomException(KakaoApiErrorCode.from(error));
        }
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
