package com.guenbon.siso.argumentresolver;

import com.guenbon.siso.dto.page.PageParam;
import com.guenbon.siso.dto.page.SortProperty;
import com.guenbon.siso.exception.BadRequestException;
import com.guenbon.siso.exception.errorCode.PageableErrorCode;
import com.guenbon.siso.support.annotation.page.PageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
public class PageParamResolver implements HandlerMethodArgumentResolver {

    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final String SORT_PARAM = "sort";

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PageConfig.class) && PageParam.class.isAssignableFrom(
                parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) {
        final PageConfig pageConfig = parameter.getParameterAnnotation(PageConfig.class);

        final int page = getIntParam(webRequest, PAGE_PARAM, pageConfig.defaultPage());
        final int size = getIntParam(webRequest, SIZE_PARAM, pageConfig.defaultSize());
        final String sort = resolveSort(webRequest.getParameter(SORT_PARAM), pageConfig);

        validatePageAndSize(page, size);

        return PageParam.of(page, size, sort);
    }

    private int getIntParam(final NativeWebRequest request, final String name, final int defaultValue) {
        final String value = request.getParameter(name);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            throw new BadRequestException(PageableErrorCode.INVALID_FORMAT);
        }
    }

    private String resolveSort(final String sort, final PageConfig pageConfig) {
        if (sort == null || !isValidSort(sort, pageConfig.allowedSorts())) {
            return pageConfig.defaultSort();
        }
        return sort;
    }

    private void validatePageAndSize(final int page, final int size) {
        if (page < 0) {
            throw new BadRequestException(PageableErrorCode.INVALID_PAGE);
        }
        if (size < 1) {
            throw new BadRequestException(PageableErrorCode.INVALID_SIZE);
        }
    }

    private boolean isValidSort(final String sort, final SortProperty[] allowedSorts) {
        if (sort == null || sort.trim().isEmpty()) {
            return true; // sort가 없으면 통과
        }

        final String[] parts = sort.split(",");
        if (parts.length == 1) {
            return isAllowedSort(parts[0].trim(), allowedSorts); // 정렬 속성만 있어도 통과
        }

        if (parts.length == 2) {
            final String property = parts[0].trim();
            final String direction = parts[1].trim().toUpperCase();
            return isAllowedSort(property, allowedSorts) && isAllowedDirection(direction);
        }
        return false;
    }

    private static boolean isAllowedDirection(final String direction) {
        if ("ASC".equals(direction) || "DESC".equals(direction)) {
            return true;
        }
        throw new BadRequestException(PageableErrorCode.UNSUPPORTED_SORT_DIRECTION);
    }

    private boolean isAllowedSort(final String property, final SortProperty[] allowedSorts) {
        for (final SortProperty allowedSort : allowedSorts) {
            if (allowedSort.getValue().equals(property)) {
                return true;
            }
        }
        throw new BadRequestException(PageableErrorCode.UNSUPPORTED_SORT_PROPERTY);
    }
}
