package com.guenbon.siso.argumentresolver;

import com.guenbon.siso.dto.page.PageParam;
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

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PageConfig.class) && PageParam.class.isAssignableFrom(
                parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        PageConfig pageConfig = parameter.getParameterAnnotation(PageConfig.class);

        int page = getIntParam(webRequest, "page", pageConfig.defaultPage());
        int size = getIntParam(webRequest, "size", pageConfig.defaultSize());
        String sort = webRequest.getParameter("sort");

        // Validate page and size constraints
        if (page < 0) {
            throw new BadRequestException(PageableErrorCode.INVALID_PAGE);
        }
        if (size < 1) {
            throw new BadRequestException(PageableErrorCode.INVALID_SIZE);
        }

        log.info("Initial sort value: {}", sort);

        if (sort == null || !isValidSort(sort, pageConfig.allowedSorts())) {
            sort = pageConfig.defaultSort();
            log.info("Sort set to default: {}", sort);
        }

        PageParam param = new PageParam();
        param.setPage(page);
        param.setSize(size);
        param.setSort(sort);

        log.info("Final PageParam: {}", param);

        return param;
    }

    private int getIntParam(NativeWebRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        try {
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            throw new BadRequestException(PageableErrorCode.INVALID_FORMAT);
        }
    }

    private boolean isValidSort(String sort, String[] allowedSorts) {
        if (sort == null || sort.trim().isEmpty()) {
            return true; // sort가 없으면 통과
        }

        String[] parts = sort.split(",");
        if (parts.length == 1) {
            return isAllowedSort(parts[0].trim(), allowedSorts); // 정렬 속성만 있어도 통과
        }

        if (parts.length == 2) {
            String property = parts[0].trim();
            String direction = parts[1].trim().toUpperCase();
            return isAllowedSort(property, allowedSorts) && isAllowedDirection(direction);
        }
        return false;
    }

    private static boolean isAllowedDirection(String direction) {
        if ("ASC".equals(direction) || "DESC".equals(direction)) {
            return true;
        } else {
            throw new BadRequestException(PageableErrorCode.UNSUPPORTED_SORT_DIRECTION);
        }
    }

    private boolean isAllowedSort(String property, String[] allowedSorts) {
        for (String allowedSort : allowedSorts) {
            if (allowedSort.equals(property)) {
                return true;
            }
        }
        throw new BadRequestException(PageableErrorCode.UNSUPPORTED_SORT_PROPERTY);
    }
}
