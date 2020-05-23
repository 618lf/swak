package com.swak.persistence;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * 多数据源标识 value
 * 
 * @author lifeng
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface DS {

	/**
	 * 配置代码执行的指定数据源
	 */
	String value() default StringUtils.EMPTY;
}
