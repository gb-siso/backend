package com.guenbon.siso.support.annotation;

import com.guenbon.siso.support.constants.MemberRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {
    MemberRole role() default MemberRole.MEMBER;
}
