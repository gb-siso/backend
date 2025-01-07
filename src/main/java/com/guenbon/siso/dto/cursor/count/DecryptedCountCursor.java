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

    public Boolean isEmpty() {
        if (idCursor == null && countCursor == null) {
            return true;
        }
        return false;
    }

    public static DecryptedCountCursor of(Long decryptedId, Integer countCursor) {
        return new DecryptedCountCursor(decryptedId, countCursor);
    }

    @Override
    public String toString() {
        return "DecryptedCountCursor{" +
                "idCursor=" + idCursor +
                ", countCursor=" + countCursor +
                '}';
    }
}
