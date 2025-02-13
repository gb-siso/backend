package com.guenbon.siso.service.member;

import com.guenbon.siso.entity.Member;
import com.guenbon.siso.exception.CustomException;
import com.guenbon.siso.exception.errorCode.MemberErrorCode;
import com.guenbon.siso.repository.MemberRepository;
import com.guenbon.siso.util.RandomNicknameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    public static final String DEFAULT_IMAGE = "default_image.jpg";
    private static final int MAX_NICKNAME_GENERATION_ATTEMPTS = 1000; // 최대 시도 횟수

    private final MemberRepository memberRepository;

    public Member findById(final Long id) {
        return memberRepository.findById(id).orElseThrow(() -> new CustomException(MemberErrorCode.NOT_EXISTS));
    }

    public Member findByKakaoIdOrCreateMember(Long kakaoId) {
        return memberRepository.findByKakaoId(kakaoId).orElseGet(() -> createMember(kakaoId));
    }

    public Member findByNaverIdOrCreateMember(String naverId) {
        return memberRepository.findByNaverId(naverId).orElseGet(() -> createMember(naverId));
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

    private Member createMember(String naverId) {
        String randomNickname;
        for (int i = 1; i <= MAX_NICKNAME_GENERATION_ATTEMPTS; i++) {
            randomNickname = RandomNicknameGenerator.generateNickname();
            log.info("nickname generated {}", randomNickname);
            boolean existsByNickname = memberRepository.existsByNickname(randomNickname);
            if (!existsByNickname) {
                return memberRepository.save(Member.from(naverId, randomNickname, DEFAULT_IMAGE));
            }
        }
        throw new CustomException(MemberErrorCode.RANDOM_NICKNAME_GENERATE_FAILED);
    }

    public Member findByRefreshToken(String refreshToken) {
        return null;
    }
}
