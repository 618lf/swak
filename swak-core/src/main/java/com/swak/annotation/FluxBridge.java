package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * 桥接服务，一般配合地三方服务时指定运行模式
 *
 * @author: lifeng
 * @date: 2020/3/28 17:14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface FluxBridge {

	/**
	 * @return 定义是否顺序执行 handler，每个Verticle 都有一个 Context，通过这个Context来提交需要运行的
	 *         HandlerHodler <br>
	 *         <p>
	 *         WorkContext 中持有一个Queue，来保证运行这个Verticle的handler是顺序的。<br>
	 *         <p>
	 *         也就是同一個Verticle 並不是并发执行的。<br>
	 *         <p>
	 *         但是在JDBC阻塞时编程中，不需要此特性。默认情况下不使用此特性 <br>
	 *         <p>
	 *         如果是非阻塞的Verticle 可以使用这个特性<br>
	 */
	Context context() default Context.Concurrent;

	/**
	 * @return 发布服务的个数
	 */
	int instances() default 1;

	/**
	 * @return 可以设置在哪个 pool 中运行
	 */
	String pool() default StringUtils.EMPTY;

	/**
	 * 桥接中启用异步
	 * 
	 * @return 是否启用异步
	 */
	boolean async() default false;
}