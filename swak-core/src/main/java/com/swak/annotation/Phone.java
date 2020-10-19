package com.swak.annotation;

import java.lang.annotation.*;

import com.swak.utils.StringUtils;

/**
 * 手机号判断
 *
 * @author: lifeng
 * @date: 2020/3/28 17:19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Phone {

    /**
     * @return 错误描述
     */
    String msg() default StringUtils.EMPTY;
}