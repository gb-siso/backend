package com.guenbon.siso.repository;

import com.guenbon.siso.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findById(Long id);
}
