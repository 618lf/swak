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
public @interface VertxService {

	/**
	 * 实例化的名称
	 * @return
	 */
	@AliasFor(annotation = Service.class)
	String value() default "";
	
	/**
	 * 是否是http
	 * @return
	 */
	boolean httpServer() default false;
	
	/**
	 * 发布服务的个数
	 * @return
	 */
	int instances() default 1;
	
	/**
	 * 可以设置在哪个 pool 中运行
	 * @return
	 */
	String use_pool() default "";
	
	/**
	 * 是否是代理类
	 * 
	 * @return
	 */
	boolean isAop() default true;
}