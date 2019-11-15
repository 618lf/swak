package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述 API
 * 
 * @ClassName:  ApiDoc   
 * @Description:TODO(描述这个类的作用)   
 * @author: lifeng
 * @date:   Nov 15, 2019 9:42:03 AM
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiDoc {

	/**
	 * 描述 API 的 基本功能，和作用
	 * 
	 * @Title: value
	 * @Description: TODO(描述)
	 * @return
	 * @author lifeng
	 * @date 2019-11-15 09:43:41
	 */
	String value() default "";
}
