package com.guenbon.siso.repository.like.rating;

import com.guenbon.siso.entity.like.RatingLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingLikeRepository extends JpaRepository<RatingLike, Long>, QuerydslRatingLikeRepository {

    Optional<RatingLike> findByRatingIdAndMemberId(Long ratingId, Long memberId);

    boolean existsByRatingIdAndMemberId(Long ratingId, Long memberId);
}
