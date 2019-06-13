package com.swak.rabbit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.netty.util.internal.StringUtil;

/**
 * 定义方法为消费者
 * @see RabbitMqPostProcessor
 * @author lifeng
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Subscribe {

	/**
	 * 监听的队列
	 * 
	 * @return
	 */
	String queue() default StringUtil.EMPTY_STRING;

	/**
	 * 每次消费的消息数
	 * 
	 * @return
	 */
	int prefetch() default 1;
	
	/**
	 * 并行度，启动的消费者数量
	 * 
	 * @return
	 */
	int parallel() default 1;
}