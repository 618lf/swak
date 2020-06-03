package com.sample.tools.plugin.config;

import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;

/**
 * 从 classpath 路径扫描类
 * 
 * @author lifeng
 * @date 2020年6月2日 下午4:58:26
 */
public class ClassPathPluginScanner extends ClassPathBeanDefinitionScanner {

	private Logger LOGGER = LoggerFactory.getLogger(ClassPathPluginScanner.class);

	@SuppressWarnings("rawtypes")
	private Class<? extends PluginFactoryBean> mapperFactoryBeanClass = PluginFactoryBean.class;

	public ClassPathPluginScanner(BeanDefinitionRegistry registry) {
		super(registry, false);
	}

	/**
	 * Configures parent scanner to search for the right interfaces. It can search
	 * for all interfaces or just for those that extends a markerInterface or/and
	 * those annotated with the annotationClass
	 */
	public void registerFilters() {
		addIncludeFilter(new PluginClassFilter());

		// exclude package-info.java
		addExcludeFilter((metadataReader, metadataReaderFactory) -> {
			String className = metadataReader.getClassMetadata().getClassName();
			return className.endsWith("package-info");
		});
	}

	/**
	 * Calls the parent search that will search and register all the candidates.
	 * Then the registered objects are post processed to set them as
	 * MapperFactoryBeans
	 */
	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (beanDefinitions.isEmpty()) {
			LOGGER.warn("No MyBatis mapper was found in '" + Arrays.toString(basePackages)
					+ "' package. Please check your configuration.");
		} else {
			processBeanDefinitions(beanDefinitions);
		}

		return beanDefinitions;
	}

	/**
	 * 处理扫描的类
	 * 
	 * @param beanDefinitions
	 */
	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		for (BeanDefinitionHolder holder : beanDefinitions) {
			GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
			String beanClassName = definition.getBeanClassName();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Creating MapperFactoryBean with name '" + holder.getBeanName() + "' and '" + beanClassName
						+ "' mapperInterface");
			}
			definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName); // issue #59
			definition.setBeanClass(this.mapperFactoryBeanClass);
			definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		}
	}
}
