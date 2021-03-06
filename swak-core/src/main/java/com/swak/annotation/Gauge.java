package com.swak.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * An annotation for marking a method of an annotated object as a gauge. Given a
 * method like this:
 * 
 * <pre>
 * <code>
 *       {@literal @}Gauge(name = "queueSize")
 *       public int getQueueSize() {
 *           return queue.size;
 *       }
 *   </code>
 * </pre>
 * 
 * A gauge for the defining class with the name {@code queueSize} will be
 * created which uses the annotated method's return value as its value.
 *
 * @author: lifeng
 * @date: 2020/3/28 17:15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
public @interface Gauge {
	/**
	 * @return The gauge's name.
	 */
	String name() default StringUtils.EMPTY;

	/**
	 * @return If {@code true}, use the given name as an absolute name. If
	 *         {@code false}, use the given name relative to the annotated class.
	 */
	boolean absolute() default true;
}
