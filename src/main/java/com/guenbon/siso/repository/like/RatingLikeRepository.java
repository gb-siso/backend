package com.guenbon.siso.repository.like;

import com.guenbon.siso.entity.like.RatingLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingLikeRepository extends JpaRepository<RatingLike, Long>, QuerydslRatingLikeRepository {

    @Query("SELECT rl FROM RatingLike rl JOIN FETCH rl.member WHERE rl.rating.id = :ratingId AND rl.member.id = :memberId")
    Optional<RatingLike> findByRatingIdAndMemberId(@Param("ratingId") Long ratingId, @Param("memberId") Long memberId);

    boolean existsByRatingIdAndMemberId(Long ratingId, Long memberId);
}
