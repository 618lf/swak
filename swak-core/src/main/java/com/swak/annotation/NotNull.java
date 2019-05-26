package com.swak.annotation;

import java.lang.annotation.*;

/**
 * 控制判断
 * 
 * @author lifeng
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface NotNull {
    String msg() default "";
}
