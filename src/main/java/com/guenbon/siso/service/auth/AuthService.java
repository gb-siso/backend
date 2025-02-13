package com.guenbon.siso.service.auth;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String PATH = "/";

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * 카카오 아이디로 jwt 토큰을 발급한다. TODO 테스트 대상
     *
     * @param kakaoId 카카오 아이디
     * @return 발급한 토큰과 회원정보를 포함한 {@link IssueTokenResult} 객체
     */
    @Transactional(readOnly = false)
    public IssueTokenResult issueTokenWithKakaoId(final Long kakaoId) {
        Member member = memberService.findByKakaoIdOrCreateMember(kakaoId);
        return issueTokens(member);
    }

    @Transactional(readOnly = false)
    public IssueTokenResult issueTokenWithNaverId(final String naverId) {
        Member member = memberService.findByNaverIdOrCreateMember(naverId);
        return issueTokens(member);
    }

    private ResponseCookie buildRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(PATH)
                .sameSite(SameSite.NONE.attributeValue())
                .build();
    }

    @Transactional(readOnly = false)
    public IssueTokenResult reissue(String refreshToken) {
        // 리프레시토큰 유효성 검증
        jwtTokenProvider.verifySignature(refreshToken);
        // 리프레시토큰 db 조회
        Member member = memberService.findByRefreshToken(refreshToken);
        // 재발급 처리
        return issueTokens(member);
    }

    private IssueTokenResult issueTokens(Member member) {
        final String reIssuedRefreshToken = jwtTokenProvider.createRefreshToken();
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        member.storeRefreshToken(reIssuedRefreshToken);
        return IssueTokenResult.of(accessToken, buildRefreshTokenCookie(reIssuedRefreshToken), member);
    }
}
