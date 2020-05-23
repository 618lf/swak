package com.swak.persistence.mapper;

import java.lang.annotation.Annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.StringUtils;

/**
 * 配置代码扫描
 * 
 * @author lifeng
 * @date 2020年4月13日 下午9:51:18
 */
public class MapperScannerConfigurer
		implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

	private ApplicationContext applicationContext;
	private String beanName;
	private String basePackage;
	private String sqlSessionTemplateBeanName;
	private Class<? extends Annotation> annotationClass;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	/**
	 * 配置当前扫描包使用的 sqlSessionTemplateBeanName
	 * 
	 * @param sqlSessionTemplateBeanName
	 */
	public void setSqlSessionTemplateBeanName(String sqlSessionTemplateBeanName) {
		this.sqlSessionTemplateBeanName = sqlSessionTemplateBeanName;
	}

	/**
	 * 配置的扫描包
	 * 
	 * @param basePackage
	 */
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	/**
	 * 配置只识别的注解
	 * 
	 * @param annotationClass
	 */
	public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	/**
	 * 设置当前bean 的名称
	 */
	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	public String getBeanName() {
		return beanName;
	}

	/**
	 * 设置上下文
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 初始化
	 */
	@Override
	public void afterPropertiesSet() throws Exception {

	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
		scanner.setAnnotationClass(this.annotationClass);
		scanner.setResourceLoader(this.applicationContext);
		scanner.setSqlSessionTemplateBeanName(this.sqlSessionTemplateBeanName);
		scanner.registerFilters();
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}
}