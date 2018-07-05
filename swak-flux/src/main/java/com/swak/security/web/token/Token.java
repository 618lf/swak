package com.swak.security.web.token;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Token 处理器
 * 
 * @author lifeng
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Token {

	/**
	 * 设置token
	 * @return
	 */
	boolean save() default false;
	
	/**
	 * 验证 -- 单不验证 save为true的请求
	 * @return
	 */
	boolean validate() default true;
	
	/**
	 * 删除token
	 * @return
	 */
	boolean remove() default true;
}