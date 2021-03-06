package com.swak.flux.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限
 * @author lifeng
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
   
	/**
	 * 需要的角色
	 * @return
	 */
	String[] roles() default {};
	
	/**
	 * 需要的权限
	 * @return
	 */
	String[] permissions() default {};
}