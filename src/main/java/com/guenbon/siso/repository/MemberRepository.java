package com.guenbon.siso.repository;

import com.guenbon.siso.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id);

    Optional<Member> findByKakaoId(Long id);

    boolean existsByNickname(String nickname);

    Optional<Member> findByNaverId(String naverId);

    Optional<Member> findByRefreshToken(String refreshToken);
}
