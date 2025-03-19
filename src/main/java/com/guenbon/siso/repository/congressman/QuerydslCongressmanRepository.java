package com.guenbon.siso.repository.congressman;

import com.guenbon.siso.dto.congressman.CongressmanGetListDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface QuerydslCongressmanRepository {
    List<CongressmanGetListDTO> getList(Pageable pageable, Long cursorId, Double cursorRating, String party,
                                        String search);
}