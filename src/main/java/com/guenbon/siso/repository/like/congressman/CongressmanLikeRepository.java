package com.guenbon.siso.repository.like.congressman;

import com.guenbon.siso.entity.like.CongressmanLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CongressmanLikeRepository extends JpaRepository<CongressmanLike, Long> {

    Optional<CongressmanLike> findByCongressmanIdAndMemberId(Long congressmanId, Long memberId);

    boolean existsByCongressmanIdAndMemberId(Long congressmanId, Long memberId);
}
