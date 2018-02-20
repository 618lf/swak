package com.swak.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 支持path 和 method 的 requestMapping 的配置
 * @author lifeng
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

	/**
	 * 支持的 path
	 * @return
	 */
	String[] value() default {};
	
	/**
	 * 支持的 method
	 * @return
	 */
	RequestMethod method() default RequestMethod.ALL;
}