package com.guenbon.siso.dto.cursor.count;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
@AllArgsConstructor
@EqualsAndHashCode
public class CountCursor {

    private String idCursor;
    @Min(value = 0, message = "countCursor는 0 이상이어야 합니다.")
    private Integer countCursor;

    @AssertTrue(message = "일부 커서만 유효할 수 없습니다.")
    public boolean isCursorValid() {
        if (isAllFieldInvalid()) { // 모든 필드 inValid
            return true;
        }
        // 일부 필드만 inValid
        return !isSomeFieldInvalid();// 모든 필드 valid
    }

    private boolean isSomeFieldInvalid() {
        return !isCountCursorValid() || !isIdCursorValid();
    }

    public boolean isAllFieldInvalid() {
        return !isCountCursorValid() && !isIdCursorValid();
    }


    public boolean isIdCursorValid() {
        return idCursor != null && !idCursor.isBlank() && !idCursor.equals("null");
    }

    public boolean isCountCursorValid() {
        return countCursor != null && countCursor >= 0;
    }

    @Override
    public String toString() {
        return "CountCursor{" +
                "idCursor='" + idCursor + '\'' +
                ", countCursor=" + countCursor +
                '}';
    }
}
