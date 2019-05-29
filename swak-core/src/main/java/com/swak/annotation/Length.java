package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * 长度校验
 * 
 * @author lifeng
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface Length {

	/**
	 * 最小值
	 * 
	 * @return
	 */
	int min() default 0;

	/**
	 * 最大值
	 * 
	 * @return
	 */
	int max() default Integer.MAX_VALUE;
	
	/**
	 * 错误描述
	 * 
	 * @return
	 */
	String msg() default StringUtils.EMPTY;
}
