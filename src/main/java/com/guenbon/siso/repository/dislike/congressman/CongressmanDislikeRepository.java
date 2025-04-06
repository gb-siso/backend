package com.guenbon.siso.repository.dislike.congressman;

import com.guenbon.siso.entity.dislike.CongressmanDisLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CongressmanDislikeRepository extends JpaRepository<CongressmanDisLike, Long> {

    Optional<CongressmanDisLike> findByCongressmanIdAndMemberId(Long congressmanId, Long memberId);

    boolean existsByCongressmanIdAndMemberId(Long congressmanId, Long memberId);
}
