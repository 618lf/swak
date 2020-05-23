package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义方法执行的超时时间
 *
 * @author: lifeng
 * @date: 2020/3/28 17:25
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TimeOut {

    /**
     * -1 使用默认的超时时间
     * 0 不超时
     */
    int value() default -1;
}