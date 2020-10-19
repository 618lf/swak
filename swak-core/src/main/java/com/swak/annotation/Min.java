package com.swak.annotation;

import com.swak.utils.StringUtils;

import java.lang.annotation.*;

/**
 * 最小值校验
 *
 * @author: lifeng
 * @date: 2020/3/28 17:19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Min {

    /**
     * @return 最小值
     */
    int value();

    /**
     * @return 错误描述
     */
    String msg() default StringUtils.EMPTY;
}
