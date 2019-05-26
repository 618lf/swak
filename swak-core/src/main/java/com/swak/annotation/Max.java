package com.swak.annotation;

import java.lang.annotation.*;

/**
 * 最大值校验
 * 
 * @author lifeng
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Max {
    int value();

    String msg() default "";
}
