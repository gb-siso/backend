package com.guenbon.siso.repository.like;

import com.guenbon.siso.entity.like.RatingLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingLikeRepository extends JpaRepository<RatingLike, Long>, QuerydslRatingLikeRepository {

}
