package com.swak.rpc.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * 标识 RPC 服务类，用于接口上
 * 
 * 自动识别Public方法，且返回值为 CompletableFuture 的 方法。
 * 
 * @author lifeng
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Component
public @interface RpcService {
	
	public static final int DEFAULT_WEIGHT = 100;
	public static final String DEFAULT_VERSION = "1.0.0";
	public static final long DEFAULT_TIME_OUT = 5 * 1000L;
	public static final boolean DEFAULT_IGNORE = false;

	/**
	 * for METHOD and TYPE, RPC服务方法版本<br>
	 * 仅第一位为实际使用，第一位不一样时表示是不兼容的方法调用<br>
	 * method有则用method的，method没有则使用class的，都没有则使用默认值1.0.0
	 * 
	 * @return
	 */
	String version() default DEFAULT_VERSION;

	/**
	 * for METHOD and TYPE, millseconds, 当等于-1时程序会自动处理为默认值5000<br>
	 * method有则用method的，method没有则使用class的，class没有则使用默认值5000
	 * 
	 * @return
	 */
	long timeout() default -1;

	/**
	 * for METHOD and TYPE，忽略，不对外提供服务
	 * 
	 * @return
	 */
	boolean ignore() default false;
}