package com.swak.config.freemarker;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.freemarker.FreeMarkerConfigurationFactory;
import com.swak.freemarker.FreeMarkerConfigurer;
import com.swak.reactivex.web.converter.TemplateHttpMessageConverter;

import freemarker.template.Template;

/**
 * Configuration for FreeMarker when used in a servlet web context.
 *
 * @author Brian Clozel
 * @author Andy Wilkinson
 */
@Configuration
@ConditionalOnClass({ Template.class })
@EnableConfigurationProperties(FreeMarkerProperties.class)
public class FreeMarkerAutoConfiguration {

	@Autowired
	private FreeMarkerProperties properties;

	@Bean
	@ConditionalOnMissingBean(FreeMarkerConfigurer.class)
	public FreeMarkerConfigurer freeMarkerConfigurer() {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		applyProperties(configurer);
		return configurer;
	}

	protected void applyProperties(FreeMarkerConfigurationFactory factory) {
		factory.setTemplateLoaderPaths(this.properties.getTemplateLoaderPath());
		factory.setPreferFileSystemAccess(this.properties.isPreferFileSystemAccess());
		factory.setDefaultEncoding(Constants.DEFAULT_ENCODING.name());
		Properties settings = new Properties();
		settings.putAll(this.properties.getSettings());
		factory.setFreemarkerSettings(settings);
		factory.setFreemarkerVariables(this.properties.getVariables());
	}

	/**
	 * Spring 模板处理
	 * 
	 * @param configurer
	 * @return
	 */
	@Bean
	public TemplateHttpMessageConverter templateHttpMessageConverter(FreeMarkerConfigurer configurer) {
		return new TemplateHttpMessageConverter(configurer.getConfiguration());
	}
}
