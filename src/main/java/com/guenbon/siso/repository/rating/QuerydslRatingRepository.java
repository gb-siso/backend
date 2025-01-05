package com.guenbon.siso.repository.rating;

import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.like.RatingLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface QuerydslRatingRepository {
    Optional<List<Rating>> getRecentRatingByCongressmanIdSort(Long congressmanId, Pageable pageable);

}
