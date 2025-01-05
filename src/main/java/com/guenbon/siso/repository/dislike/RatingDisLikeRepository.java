package com.guenbon.siso.repository.dislike;

import com.guenbon.siso.entity.dislike.RatingDisLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingDisLikeRepository extends JpaRepository<RatingDisLike, Long>, QuerydslRatingDisLikeRepository {

}
