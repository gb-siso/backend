package com.guenbon.siso.repository.dislike;

import com.guenbon.siso.entity.dislike.RatingDislike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingDislikeRepository extends JpaRepository<RatingDislike, Long>, QuerydslRatingDislikeRepository {

}
