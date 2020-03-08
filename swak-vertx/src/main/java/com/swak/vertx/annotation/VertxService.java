package com.swak.vertx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import com.swak.utils.StringUtils;

/**
 * 用于配置服务
 * @author lifeng
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface VertxService {

	/**
	 * 实例化的名称
	 * 
	 * @return
	 */
	@AliasFor(annotation = Service.class)
	String value() default StringUtils.EMPTY;

	/**
	 * 是否是http
	 * 
	 * @return
	 */
	boolean http() default false;

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
	 * 是否是代理类, 自动获取代理类，不需要标记，过渡方法
	 * 
	 * @return
	 */
	@Deprecated
	boolean isAop() default true;
	
	/**
	 * 定义是否顺序执行 handler，每个Verticle 都有一个 Context，通过这个Context来提交需要运行的 HandlerHodler <br>
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
	boolean ordered() default false;

	/**
	 * 指定服务类
	 * 
	 * @return
	 */
	Class<?> service() default void.class;
}