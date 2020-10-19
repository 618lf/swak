package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 支持path 和 method 的 requestMapping 的配置
 *
 * @author: lifeng
 * @date: 2020/3/28 17:20
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    /**
     * @return 支持的 path
     */
    String[] value() default {};

    /**
     * @return 支持的 method
     */
    RequestMethod method() default RequestMethod.ALL;
}