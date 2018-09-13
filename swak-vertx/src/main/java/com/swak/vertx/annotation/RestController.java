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
	 * 设置 bean name， 不能用其他的替换，识别不到
	 * @return
	 */
	@AliasFor(annotation = Controller.class, attribute="value")
	String value() default "";
	
	/**
	 * 支持的 path
	 * @return
	 */
	@AliasFor(annotation = RequestMapping.class, value="value")
	String[] path() default {};
	
	/**
	 * 支持的 method
	 * @return
	 */
	@AliasFor(annotation = RequestMapping.class)
	RequestMethod method() default RequestMethod.ALL;
}
