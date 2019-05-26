package com.swak.annotation;

import java.lang.annotation.*;

/**
 * 手机号判断
 * 
 * @author lifeng
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Phone {
    String msg() default "";
}