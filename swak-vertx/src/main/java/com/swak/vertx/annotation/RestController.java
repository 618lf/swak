package com.swak.vertx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

/**
 * 用于配置请求
 * @author lifeng
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@RequestMapping
public @interface RestController {
	
	/**
	 * 支持的 path
	 * @return
	 */
	@AliasFor(annotation = RequestMapping.class)
	String[] value() default {};
	
	/**
	 * 支持的 method
	 * @return
	 */
	@AliasFor(annotation = RequestMapping.class)
	RequestMethod method() default RequestMethod.ALL;
	
	/**
	 * 设置 bean name
	 * @return
	 */
	@AliasFor(annotation = Controller.class, value="value")
	String name() default "";
}
