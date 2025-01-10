package com.guenbon.siso.dto.cursor.count;

import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.CursorErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@Slf4j
public class CountCursor {

    private String idCursor;
    private Integer countCursor;

    public static CountCursor of(String idCursor, Integer countCursor) {
        if (isAllFieldNull(idCursor, countCursor)) {
            return null;
        }
        validateCursor(idCursor, countCursor);
        return new CountCursor(idCursor, countCursor);
    }

    private static boolean isAllFieldNull(String idCursor, Integer countCursor) {
        return idCursor == null && countCursor == null;
    }

    private static void validateCursor(String idCursor, Integer countCursor) {
        if ((idCursor == null || idCursor.isEmpty()) || countCursor == null) {
            throw new BadRequestException(CursorErrorCode.NULL_OR_EMPTY_VALUE);
        }
        if (countCursor < 0) {
            throw new BadRequestException(CursorErrorCode.NEGATIVE_VALUE);
        }
    }

    @Override
    public String toString() {
        return "CountCursor{" +
                "idCursor='" + idCursor + '\'' +
                ", countCursor=" + countCursor +
                '}';
    }
}
