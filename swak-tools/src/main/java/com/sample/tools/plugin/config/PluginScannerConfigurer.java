package com.sample.tools.plugin.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

public class PluginScannerConfigurer
		implements BeanDefinitionRegistryPostProcessor,  ApplicationContextAware {

	private ApplicationContext applicationContext;
	private String basePackage;

	/**
	 * 配置的扫描包
	 * 
	 * @param basePackage
	 */
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		ClassPathPluginScanner scanner = new ClassPathPluginScanner(registry);
		scanner.setResourceLoader(this.applicationContext);
		scanner.registerFilters();
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}
}
