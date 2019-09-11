package com.swak.config.freemarker.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

import com.swak.freemarker.FreeMarkerConfigurer;
import com.swak.vertx.handler.converter.TemplateHttpMessageConverter;

/**
 * 
 * @author lifeng
 */
@ConditionalOnClass({ TemplateHttpMessageConverter.class })
public class VertxTemplateConfiguration {

	@Bean
	public TemplateHttpMessageConverter templateHttpMessageConverter(FreeMarkerConfigurer freeMarkerConfigurer) {
		return new TemplateHttpMessageConverter(freeMarkerConfigurer.getConfiguration());
	}
}
