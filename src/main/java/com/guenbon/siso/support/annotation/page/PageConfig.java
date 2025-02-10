package com.guenbon.siso.support.annotation.page;

import com.guenbon.siso.support.constants.SortProperty;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageConfig {
    SortProperty[] allowedSorts() default SortProperty.ID; // 허용된 sort 값

    String defaultSort() default "id, DESC"; // 기본 sort

    int defaultPage() default 1; // 기본 page

    int defaultSize() default 10; // 기본 size
}
