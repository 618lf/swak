package com.swak.reactivex.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.Constants;

/**
 * 异步执行代码
 * @author lifeng
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Async {
	/**
	 * 指定在哪个线程池中
	 * @return
	 */
	String value() default Constants.default_pool;
}