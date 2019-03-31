package com.swak.flux.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * 参数级别
 * @author lifeng
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PathVariable {
	/**
	 * 可以设置值
	 * @return
	 */
	String value() default StringUtils.EMPTY;
}