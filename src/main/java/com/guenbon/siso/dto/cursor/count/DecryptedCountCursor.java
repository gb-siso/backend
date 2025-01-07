package com.guenbon.siso.dto.cursor.count;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DecryptedCountCursor {
    private Long idCursor;
    private Integer countCursor;

    private static DecryptedCountCursor of(Long idCursor, Integer countCursor) {
        return new DecryptedCountCursor(idCursor, countCursor);
    }

    public Boolean isEmpty() {
        if (idCursor == null && countCursor == null) {
            return true;
        }
        return false;
    }
}
