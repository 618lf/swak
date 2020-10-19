package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * 控制判断
 *
 * @author: lifeng
 * @date: 2020/3/28 17:19
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface NotNull {

    /**
     * @return 错误描述
     */
    String msg() default StringUtils.EMPTY;
}
