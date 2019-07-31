package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * FIELD 是json形式，用于将参数的转换
 * 
 * @author lifeng
 */
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Header {

	/**
	 * 可以为空
	 * 
	 * @return
	 */
	String name() default StringUtils.EMPTY;
}