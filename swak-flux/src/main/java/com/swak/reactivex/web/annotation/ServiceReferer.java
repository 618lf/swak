package com.swak.reactivex.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.Constants;

/**
 * 服务的注册 - 不能使用代理方式，应该使用javassist的方式创建一个类和对象
 * 来实现异步接口，对同步方式简单的调用，通过 completeTableFuture 来 驱动
 * -- 暫時沒有實現
 * @author lifeng
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ServiceReferer {

	/**
	 * 指定在哪个线程池中
	 * @return
	 */
	String value() default Constants.default_pool;
}