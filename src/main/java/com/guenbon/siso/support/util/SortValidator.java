package com.guenbon.siso.support.util;

import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.PageableErrorCode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;

@Slf4j
public class SortValidator {

    private SortValidator() {
        // 유틸 클래스의 인스턴스화를 방지
    }

    public static void validateSortProperties(Sort sort, List<String> allowedSortProperties) {
        sort.forEach(order -> {
            if (!allowedSortProperties.contains(order.getProperty())) {
                throw new BadRequestException(PageableErrorCode.UNSUPPORTED_SORT_PROPERTY);
            }
        });
    }
}
