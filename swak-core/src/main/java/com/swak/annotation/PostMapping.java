package com.swak.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Post url mapping
 *
 * @author: lifeng
 * @date: 2020/3/28 17:20
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = RequestMethod.POST)
public @interface PostMapping {

    /**
     * 支持的 path
     */
    @AliasFor(annotation = RequestMapping.class)
    String[] value() default {};
}
