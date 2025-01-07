package com.guenbon.siso.repository.rating;

import com.guenbon.siso.dto.cursor.count.DecryptedCountCursor;
import com.guenbon.siso.entity.Rating;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface QuerydslRatingRepository {
    List<Rating> getSortedRatingsByCongressmanId(Long congressmanId, Pageable pageable,
                                                 DecryptedCountCursor countCursor);
}
