package com.guenbon.siso.service.member;

import com.guenbon.siso.dto.auth.IssueTokenResult;
import com.guenbon.siso.entity.Member;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.MemberErrorCode;
import com.guenbon.siso.repository.MemberRepository;
import com.guenbon.siso.service.auth.JwtTokenProvider;
import com.guenbon.siso.util.RandomNicknameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String PATH = "/";
    public static final String DEFAULT_IMAGE = "default_image.jpg";
    private static final int MAX_NICKNAME_GENERATION_ATTEMPTS = 1000; // 최대 시도 횟수

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Member findById(final Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new CustomException(MemberErrorCode.NOT_EXISTS));
    }

    /**
     * 카카오 아이디로 jwt 토큰을 발급한다. TODO 테스트 대상
     *
     * @param kakaoId 카카오 아이디
     * @return 발급한 토큰과 회원정보를 포함한 {@link IssueTokenResult} 객체
     */
    @Transactional(readOnly = false)
    public IssueTokenResult issueToken(final Long kakaoId) {
        Member member = memberRepository.findByKakaoId(kakaoId).orElseGet(() -> createMember(kakaoId));
        final String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        final String accessToken = jwtTokenProvider.createAccessToken(member.getId());
        member.storeRefreshToken(refreshToken);
        return IssueTokenResult.of(accessToken, buildRefreshTokenCookie(refreshToken), member);
    }

    /**
     * 랜덤한 닉네임을 가진 회원을 생성한다.
     *
     * @param kakaoId 카카오 아이디
     * @return 생성한 회원 객체
     */
    private Member createMember(Long kakaoId) {
        String randomNickname;
        for (int i = 1; i <= MAX_NICKNAME_GENERATION_ATTEMPTS; i++) {
            randomNickname = RandomNicknameGenerator.generateNickname();
            log.info("nickname generated {}", randomNickname);
            boolean existsByNickname = memberRepository.existsByNickname(randomNickname);
            if (!existsByNickname) {
                return memberRepository.save(Member.from(kakaoId, randomNickname, DEFAULT_IMAGE));
            }
        }
        throw new CustomException(MemberErrorCode.RANDOM_NICKNAME_GENERATE_FAILED);
    }

    private ResponseCookie buildRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .path(PATH)
                .sameSite(SameSite.NONE.attributeValue())
                .build();
    }
}
