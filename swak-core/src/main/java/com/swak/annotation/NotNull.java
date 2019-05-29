package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * 控制判断
 * 
 * @author lifeng
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface NotNull {
	
	/**
	 * 错误描述
	 * 
	 * @return
	 */
	String msg() default StringUtils.EMPTY;
}
