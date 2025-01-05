package com.guenbon.siso.repository.rating;

import com.guenbon.siso.entity.Rating;
import com.guenbon.siso.entity.like.RatingLike;
import com.guenbon.siso.repository.like.QuerydslRatingLikeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public class QuerydslRatingRepositoryImpl implements QuerydslRatingLikeRepository {

    public Optional<List<Rating>> getRecentRatingByCongressmanIdSort(Long congressmanId, Pageable pageable) {
        return Optional.empty();
    }
}
