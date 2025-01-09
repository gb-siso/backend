package com.guenbon.siso.argumentresolver;

import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.PageableErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
public class CustomPageableArgumentResolver extends PageableHandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return super.supportsParameter(parameter);
    }

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        // 요청 파라미터 확인
        final String sort = webRequest.getParameter("sort");
        final String page = webRequest.getParameter("page");
        final String size = webRequest.getParameter("size");
        // 위 변수 3개에 대해 로그찍기
        log.info("sort : {}, page : {}, size : {}", sort, page, size);

        // Pageable 관련 파라미터가 있는 경우만 검증 수행
        if (sort != null || page != null || size != null) {
            log.info("Pageable parameters found. Validating...");
            try {
                validatePageable(page, size, sort);
            } catch (NumberFormatException e) {
                throw new BadRequestException(PageableErrorCode.INVALID_FORMAT);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException(PageableErrorCode.UNSUPPORTED_SORT_DIRECTION);
            }
        } else {
            log.info("No Pageable parameters found. Skipping validation.");
        }

        // 기본 동작 수행
        return super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
    }


    private void validatePageable(String pageParameter, String sizeParameter, String sortParameter) {
        validatePageParameter(pageParameter);
        validateSizeParameter(sizeParameter);
        validateSortDirection(sortParameter);
    }

    private void validatePageParameter(String pageParameter) {
//        if (pageParameter == null) {
//            throw new BadRequestException(PageableErrorCode.NULL_VALUE);
//        }
        if (pageParameter != null) {
            int intPage = Integer.parseInt(pageParameter);
            if (intPage < 0) {
                throw new BadRequestException(PageableErrorCode.INVALID_PAGE);
            }
        }
    }

    private void validateSizeParameter(String sizeParameter) {
//        if (sizeParameter == null) {
//            throw new BadRequestException(PageableErrorCode.NULL_VALUE);
//        }
        if (sizeParameter != null) {
            int intSize = Integer.parseInt(sizeParameter);
            if (intSize < 1) {
                throw new BadRequestException(PageableErrorCode.INVALID_SIZE);
            }
        }
    }

    private void validateSortDirection(String sortParameter) {
        if (sortParameter != null && sortParameter.contains(",")) {
            String[] split = sortParameter.split(",");

            // Ensure it contains exactly two parts: property and direction
            if (split.length != 2) {
                throw new IllegalArgumentException();
            }

            // Validate direction (must be ASC or DESC)
            String directionPart = split[1].trim();
            Direction.fromString(directionPart); // Throws IllegalArgumentException if invalid
        }
    }
}
