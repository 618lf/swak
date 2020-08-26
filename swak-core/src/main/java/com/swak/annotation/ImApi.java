package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

import com.swak.utils.StringUtils;

/**
 * 用于配置Ws请求
 *
 * @author: lifeng
 * @date: 2020/3/28 17:22
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@ImMapping
public @interface ImApi {

	/**
	 * 设置 bean name， 不能用其他的替换，识别不到
	 */
	@AliasFor(annotation = Controller.class, attribute = "value")
	String value() default StringUtils.EMPTY;

	/**
	 * 支持的 method
	 */
	@AliasFor(annotation = ImMapping.class)
	ImOps method() default ImOps.All;

	/**
	 * 发布的端口
	 */
	int port() default -1;
}