package com.guenbon.siso.dto.cursor.count;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CountCursor {
    private String idCursor;
    private Integer countCursor;

    public static CountCursor of(String idCursor, Integer countCursor) {
        return new CountCursor(idCursor, countCursor);
    }

    public Boolean isEmpty() {
        if ((idCursor == null || idCursor.isEmpty()) && (countCursor == null)) {
            return true;
        }
        return false;
    }
}
