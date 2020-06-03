package com.sample.tools.plugin.config;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * 插件扫描
 * 
 * @author lifeng
 * @date 2020年6月2日 下午4:50:35
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(PluginScannerRegistrar.class)
public @interface PluginScan {

	String[] value() default {};

	String[] basePackages() default {};
}