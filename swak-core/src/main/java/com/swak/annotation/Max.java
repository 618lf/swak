package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * 最大值校验
 * 
 * @author lifeng
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface Max {
	
	/**
	 * 最大值
	 * 
	 * @return
	 */
	int value();

	/**
	 * 错误描述
	 * 
	 * @return
	 */
	String msg() default StringUtils.EMPTY;
}
