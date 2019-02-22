package com.swak.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.env.Environment;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@BootstrapWith(ApplicationTestContextBootstrapper.class)
public @interface ApplicationTest {

	/**
	 * Alias for {@link #properties()}.
	 * 
	 * @return the properties to apply
	 */
	@AliasFor("properties")
	String[] value() default {};

	/**
	 * Properties in form {@literal key=value} that should be added to the Spring
	 * {@link Environment} before the test runs.
	 * 
	 * @return the properties to add
	 */
	@AliasFor("value")
	String[] properties() default {};

	/**
	 * The <em>annotated classes</em> to use for loading an
	 * {@link org.springframework.context.ApplicationContext ApplicationContext}.
	 * Can also be specified using
	 * {@link ContextConfiguration#classes() @ContextConfiguration(classes=...)}. If
	 * no explicit classes are defined the test will look for nested
	 * {@link Configuration @Configuration} classes, before falling back to a
	 * {@link SpringBootConfiguration} search.
	 * 
	 * @see ContextConfiguration#classes()
	 * @return the annotated classes used to load the application context
	 */
	Class<?>[] classes() default {};
}
