package com.swak.annotation;

import java.lang.annotation.*;

/**
 * 最小值校验
 * 
 * @author lifeng
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Min {
    int value();

    String msg() default "";
}
