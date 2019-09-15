package com.swak.config.freemarker;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.swak.Constants;
import com.swak.config.freemarker.web.VertxTemplateConfiguration;
import com.swak.freemarker.FreeMarkerConfigurationFactory;
import com.swak.freemarker.FreeMarkerConfigurer;

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
@Import({ VertxTemplateConfiguration.class })
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
		Properties settings = this.getDefaultProperties();
		settings.putAll(this.properties.getSettings());
		factory.setFreemarkerSettings(settings);
		factory.setFreemarkerVariables(this.properties.getVariables());
	}

	private Properties getDefaultProperties() {
		Properties settings = new Properties();
		settings.put("defaultEncoding", Constants.DEFAULT_ENCODING.name());
		settings.put("url_escaping_charset", Constants.DEFAULT_ENCODING.name());
		settings.put("locale", "zh_CN");
		settings.put("template_update_delay", "0");
		settings.put("tag_syntax", "auto_detect");
		settings.put("whitespace_stripping", "true");
		settings.put("classic_compatible", "true");
		settings.put("number_format", "0");
		settings.put("boolean_format", "true,false");
		settings.put("datetime_format", "yyyy-MM-dd HH:mm:ss");
		settings.put("date_format", "yyyy-MM-dd");
		settings.put("time_format", "HH:mm:ss");
		settings.put("object_wrapper", "freemarker.ext.beans.BeansWrapper");
		return settings;
	}
}
