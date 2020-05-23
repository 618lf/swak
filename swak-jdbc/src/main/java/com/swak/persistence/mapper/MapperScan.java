package com.swak.persistence.mapper;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 自定义扫描： Mapper -- 一般用于多数据源的配置中
 * 
 * @author lifeng
 * @date 2020年4月13日 下午9:52:01
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MapperScannerRegistrar.class)
public @interface MapperScan {

	/**
	 * Alias for the {@link #basePackages()} attribute. Allows for more concise
	 * annotation declarations e.g.: {@code @MapperScan("org.my.pkg")} instead of
	 * {@code @MapperScan(basePackages = "org.my.pkg"})}.
	 *
	 * @return base package names
	 */
	String[] value() default {};

	/**
	 * Base packages to scan for MyBatis interfaces. Note that only interfaces with
	 * at least one method will be registered; concrete classes will be ignored.
	 *
	 * @return base package names for scanning mapper interface
	 */
	String[] basePackages() default {};

	/**
	 * This property specifies the annotation that the scanner will search for.
	 * <p>
	 * The scanner will register all interfaces in the base package that also have
	 * the specified annotation.
	 * <p>
	 * Note this can be combined with markerInterface.
	 *
	 * @return the annotation that the scanner will search for
	 */
	Class<? extends Annotation> annotationClass() default Mapper.class;

	/**
	 * Specifies which {@code SqlSessionTemplate} to use in the case that there is
	 * more than one in the spring context. Usually this is only needed when you
	 * have more than one datasource.
	 *
	 * @return the bean name of {@code SqlSessionTemplate}
	 */
	String sqlSessionTemplateRef() default "";

}
