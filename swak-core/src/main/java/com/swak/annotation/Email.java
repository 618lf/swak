package com.swak.annotation;

import java.lang.annotation.*;

import com.swak.utils.StringUtils;

/**
 * 邮箱
 *
 * @author: lifeng
 * @date: 2020/3/28 17:13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface Email {

	/**
	 * @return 错误描述
	 */
	String msg() default StringUtils.EMPTY;
}