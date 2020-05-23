package com.swak.annotation;

import com.swak.utils.StringUtils;

import java.lang.annotation.*;

/**
 * 描述 API
 *
 * @author: lifeng
 * @date: Nov 15, 2019 9:42:03 AM
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiDoc {

    /**
     * 描述 API 的 基本功能，和作用
     *
     * @return 注释
     * @author lifeng
     * @date 2019-11-15 09:43:41
     */
    String value() default StringUtils.EMPTY;
}
