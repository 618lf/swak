package com.swak.reactivex.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;

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
	String[] value() default {};
	
	/**
	 * 支持的 method
	 * @return
	 */
	RequestMethod method() default RequestMethod.ALL;
}
