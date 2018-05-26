package com.swak;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.swak.config.ActuatorAutoConfiguration;
import com.swak.config.AppAutoConfiguration;
import com.swak.config.CacheAutoConfiguration;
import com.swak.config.DataBaseAutoConfiguration;
import com.swak.config.WebAutoConfiguration;

/**
 * 添加需要的配置类
 * @author lifeng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
@Import({
	DataBaseAutoConfiguration.class,
	CacheAutoConfiguration.class,
	WebAutoConfiguration.class,
	AppAutoConfiguration.class,
	ActuatorAutoConfiguration.class,
})
public @interface ApplicationBoot {

}