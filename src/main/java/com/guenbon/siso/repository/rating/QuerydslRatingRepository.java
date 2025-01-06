package com.guenbon.siso.repository.rating;

import com.guenbon.siso.entity.Rating;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

public interface QuerydslRatingRepository {
    Optional<List<Rating>> getRecentRatingByCongressmanId(Long congressmanId, Pageable pageable);
}
