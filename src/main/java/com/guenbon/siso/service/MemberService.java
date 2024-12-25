package com.guenbon.siso.service;

import com.guenbon.siso.entity.Member;
import com.guenbon.siso.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member findById(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.get();
    }
}
