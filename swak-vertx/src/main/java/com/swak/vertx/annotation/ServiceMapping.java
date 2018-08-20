package com.swak.vertx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

/**
 * 用于配置服务
 * @author lifeng
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface ServiceMapping {

	@AliasFor(annotation = Service.class)
	String value() default "";
	
	/**
	 * 可以设置在哪个 pool 中运行
	 * @return
	 */
	String use_pool() default "";
}