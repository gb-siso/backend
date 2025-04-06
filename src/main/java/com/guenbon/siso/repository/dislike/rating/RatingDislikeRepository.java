package com.guenbon.siso.repository.dislike.rating;

import com.guenbon.siso.entity.dislike.RatingDislike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingDislikeRepository extends JpaRepository<RatingDislike, Long>, QuerydslRatingDislikeRepository {

    Optional<RatingDislike> findByRatingIdAndMemberId(Long ratingId, Long memberId);

    boolean existsByRatingIdAndMemberId(Long ratingId, Long memberId);
}
