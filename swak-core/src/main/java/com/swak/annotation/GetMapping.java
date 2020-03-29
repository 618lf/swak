package com.swak.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Get url mapping
 *
 * @author: lifeng
 * @date: 2020/3/28 17:16
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = RequestMethod.GET)
public @interface GetMapping {

    /**
     * 支持的 path
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};
}