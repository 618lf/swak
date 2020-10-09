package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * 配置表
 * 
 * @author lifeng
 * @date 2020年10月7日 下午10:38:03
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Table {

	/**
	 * 配置表名，默认按照驼峰取表名
	 */
	String value() default StringUtils.EMPTY;
}