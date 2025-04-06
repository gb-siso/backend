package com.guenbon.siso.repository.rating;

import com.guenbon.siso.dto.cursor.count.DecryptedCountCursor;
import com.guenbon.siso.entity.Rating;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuerydslRatingRepository {
    List<Rating> getSortedRatingsByCongressmanId(Long congressmanId, Pageable pageable,
                                                 DecryptedCountCursor countCursor);
}
