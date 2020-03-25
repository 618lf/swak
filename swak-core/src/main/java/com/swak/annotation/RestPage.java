package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

/**
 * 和 @RestController 功能一致
 * 
 * @author lifeng
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestApi
public @interface RestPage {

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
