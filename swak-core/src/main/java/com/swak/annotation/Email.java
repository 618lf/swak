package com.swak.annotation;

import java.lang.annotation.*;

import com.swak.utils.StringUtils;

/**
 * 邮箱判断
 * 
 * @author lifeng
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface Email {
	
	/**
	 * 错误描述
	 * 
	 * @return
	 */
	String msg() default StringUtils.EMPTY;
}