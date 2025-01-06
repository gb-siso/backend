package com.guenbon.siso.repository.rating;

import com.guenbon.siso.entity.Rating;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface QuerydslRatingRepository {
    List<Rating> getRecentRatingByCongressmanId(Long congressmanId, Pageable pageable);
}
