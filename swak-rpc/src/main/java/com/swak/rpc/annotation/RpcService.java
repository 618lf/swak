package com.swak.rpc.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 服务接口声明，以及服务接口、方法配置，<br>
 * 配置在接口上
 * @author lifeng
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface RpcService {

	public static final int DEFAULT_WEIGHT = 100;
	public static final String DEFAULT_GROUP = "DEFAULT_GROUP";
	public static final String DEFAULT_APP = "DEFAULT_APP";
	public static final String DEFAULT_VERSION = "1.0.0";
	public static final long DEFAULT_TIME_OUT = 5 * 1000L;
	public static final boolean DEFAULT_IGNORE = false;
	
	/**
	 * 版本
	 * @return
	 */
	String version() default DEFAULT_VERSION;
	
	/**
	 * 执行的超时时间
	 * @return
	 */
	long timeout() default -1;
	
	/**
	 * 忽略，不对外提供服务
	 * @return
	 */
	boolean ignore() default DEFAULT_IGNORE;
	
	/**
	 * 将 method 设置为 rest 服务，会合并接口上的path
	 * @return
	 */
	String rest() default "";
}