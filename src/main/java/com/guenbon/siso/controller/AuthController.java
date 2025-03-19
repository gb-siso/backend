package com.guenbon.siso.controller;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.dto.auth.response.LoginDTO;
import com.guenbon.siso.exception.ApiException;
import com.guenbon.siso.exception.errorCode.KakaoApiErrorCode;
import com.guenbon.siso.exception.errorCode.NaverApiErrorCode;
import com.guenbon.siso.service.auth.AuthApiService;
import com.guenbon.siso.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthApiService authApiService;
    private final AuthService authService;

    /**
     * 카카오 로그인
     *
     * @param code             인가코드 (성공 시)
     * @param error            에러코드 (실패 시)
     * @param errorDescription 에러 설명
     * @return
     */
    @GetMapping("/login/kakao")
    public ResponseEntity<LoginDTO> kakaoLogin(@RequestParam(required = false) String code,
                                               @RequestParam(required = false) String error,
                                               @RequestParam(required = false, name = "error_description") String errorDescription) {
        log.info("카카오 인가코드 error : {}, error_description : {}", error, errorDescription);
        handleKakaoError(error);
        final IssueTokenResult issueTokenResult = authApiService.authenticateWithKakao(code);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, issueTokenResult.getRefreshTokenCookie())
                .body(LoginDTO.from(issueTokenResult));
    }

    @GetMapping("/login/naver")
    public ResponseEntity<LoginDTO> naverLogin(@RequestParam(required = false) String code,
                                               @RequestParam(required = false) String state,
                                               @RequestParam(required = false) String error,
                                               @RequestParam(required = false, name = "error_description") String errorDescription) {
        // log
        log.info("네이버 인가코드 error : {}, error_description : {}", error, errorDescription);
        log.info("네이버 코드 : {} , 네이버 state : {}", code, state);
        // 인가코드 받기 예외처리
        handleNaverError(error);
        final IssueTokenResult issueTokenResult = authApiService.authenticateWithNaver(code, state);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, issueTokenResult.getRefreshTokenCookie())
                .body(LoginDTO.from(issueTokenResult));
    }

    // 쿼리 파라미터로 예외처리하므로 컨트롤러단에서 처리
    private static void handleKakaoError(String error) {
        if (error != null) {
            throw new ApiException(KakaoApiErrorCode.from(error));
        }
    }

    private static void handleNaverError(String error) {
        if (error != null) {
            throw new ApiException(NaverApiErrorCode.from(error));
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginDTO> kakaoReissue(@CookieValue(name = "refreshToken", required = true) String refreshToken) {
        IssueTokenResult issueTokenResult = authService.reissue(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, issueTokenResult.getRefreshTokenCookie())
                .body(LoginDTO.from(issueTokenResult));
    }

    @DeleteMapping
    public ResponseEntity<Void> logout(Long memberId) {
        return null;
    }
}
