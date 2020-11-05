package com.swak.async.persistence.tx;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步事务的生命式定义
 * 
 * @author lifeng
 * @date 2020年10月8日 下午9:32:53
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Transactional {

	/**
	 * 是否只读
	 * 
	 * @return
	 */
	boolean readOnly() default false;

	/**
	 * 需要回滚的异常
	 * 
	 * @return
	 */
	Class<? extends Throwable>[] rollbackFor() default {};
}