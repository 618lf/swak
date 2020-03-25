package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import com.swak.utils.StringUtils;

/**
 * 注册服务
 * 
 * @author lifeng
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface FluxService {

	/**
	 * 实例化的名称
	 * 
	 * @return
	 */
	@AliasFor(annotation = Service.class)
	String value() default StringUtils.EMPTY;

	/**
	 * 运行的模式
	 * 
	 * @return
	 */
	Server server() default Server.EventBus;

	/**
	 * 定义是否顺序执行 handler，每个Verticle 都有一个 Context，通过这个Context来提交需要运行的 HandlerHodler
	 * <br>
	 * 
	 * WorkContext 中持有一个Queue，来保证运行这个Verticle的handler是顺序的。<br>
	 * 
	 * 也就是同一個Verticle 並不是并发执行的。<br>
	 * 
	 * 但是在JDBC阻塞时编程中，不需要此特性。默认情况下不使用此特性 <br>
	 * 
	 * 如果是非阻塞的Verticle 可以使用这个特性<br>
	 * 
	 * @return
	 */
	Context context() default Context.Concurrent;

	/**
	 * 发布服务的个数
	 * 
	 * @return
	 */
	int instances() default 1;

	/**
	 * 可以设置在哪个 pool 中运行
	 * 
	 * @return
	 */
	String use_pool() default StringUtils.EMPTY;

	/**
	 * 指定服务类
	 * 
	 * @return
	 */
	Class<?> service() default void.class;
}