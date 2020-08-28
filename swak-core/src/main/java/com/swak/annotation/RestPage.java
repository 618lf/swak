package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

/**
 * 和 @RestApi 功能一致
 *
 * @author: lifeng
 * @date: 2020/3/28 17:23
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestApi
public @interface RestPage {

	/**
	 * 设置 bean name， 不能用其他的替换，识别不到
	 */
	@AliasFor(annotation = Controller.class, attribute = "value")
	String value() default "";

	/**
	 * 支持的 path
	 */
	@AliasFor(annotation = RequestMapping.class, value = "value")
	String[] path() default {};

	/**
	 * 支持的 method
	 */
	@AliasFor(annotation = RequestMapping.class)
	RequestMethod method() default RequestMethod.ALL;

	/**
	 * 发布的端口
	 */
	@AliasFor(annotation = RestApi.class)
	int port() default -1;
	
    /**
     * 发布服务的个数
     */
	@AliasFor(annotation = RestApi.class)
    int instances() default -1;
}
