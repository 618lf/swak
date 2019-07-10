package com.swak.rabbit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.netty.util.internal.StringUtil;

/**
 * 定义方法为生产者
 * @see RabbitMqPostProcessor
 * @author lifeng
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Publisher {
	
	/**
	 * 队列
	 * 
	 * @return
	 */
	String queue() default StringUtil.EMPTY_STRING;
	
	/**
	 * 路由
	 * 
	 * @return
	 */
	String routing() default StringUtil.EMPTY_STRING;
}