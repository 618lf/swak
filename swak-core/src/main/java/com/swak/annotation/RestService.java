package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.swak.utils.StringUtils;

/**
 * 直接将路由映射到服务上
 *
 * @author: lifeng
 * @date: 2020/3/28 17:23
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping
@FluxService
@RestApi
public @interface RestService {

	/**
	 * 实例化的名称
	 */
	@AliasFor(annotation = FluxService.class)
	String value() default StringUtils.EMPTY;

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
	 * 定义是否顺序执行 handler，每个Verticle 都有一个 Context，通过这个Context来提交需要运行的 HandlerHodler
	 * <br>
	 * <p>
	 * WorkContext 中持有一个Queue，来保证运行这个Verticle的handler是顺序的。<br>
	 * <p>
	 * 也就是同一個Verticle 並不是并发执行的。<br>
	 * <p>
	 * 但是在JDBC阻塞时编程中，不需要此特性。默认情况下不使用此特性 <br>
	 * <p>
	 * 如果是非阻塞的Verticle 可以使用这个特性<br>
	 */
	@AliasFor(annotation = FluxService.class)
	Context context() default Context.Concurrent;

	/**
	 * 发布服务的个数
	 */
	@AliasFor(annotation = FluxService.class)
	int instances() default 1;

	/**
	 * 可以设置在哪个 pool 中运行
	 */
	@AliasFor(annotation = FluxService.class)
	String use_pool() default StringUtils.EMPTY;

	/**
	 * 指定服务类
	 */
	@AliasFor(annotation = FluxService.class)
	Class<?> service() default void.class;

	/**
	 * 发布的端口
	 */
	@AliasFor(annotation = RestApi.class)
	int port() default -1;
}
