package com.sample.tools.plugin.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * 插件扫描路劲注册
 * 
 * @author lifeng
 * @date 2020年6月2日 下午4:51:33
 */
public class PluginScannerRegistrar implements ImportBeanDefinitionRegistrar {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes mapperScanAttrs = AnnotationAttributes
				.fromMap(importingClassMetadata.getAnnotationAttributes(PluginScan.class.getName()));
		if (mapperScanAttrs != null) {
			registerBeanDefinitions(importingClassMetadata, mapperScanAttrs, registry,
					generateBaseBeanName(importingClassMetadata, 0));
		}
	}

	void registerBeanDefinitions(AnnotationMetadata annoMeta, AnnotationAttributes annoAttrs,
			BeanDefinitionRegistry registry, String beanName) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(PluginScannerConfigurer.class);

		List<String> basePackages = new ArrayList<>();
		basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText)
				.collect(Collectors.toList()));

		basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText)
				.collect(Collectors.toList()));

		if (basePackages.isEmpty()) {
			basePackages.add(getDefaultBasePackage(annoMeta));
		}

		builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));
		registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
	}

	private String getDefaultBasePackage(AnnotationMetadata importingClassMetadata) {
		return ClassUtils.getPackageName(importingClassMetadata.getClassName());
	}

	private String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
		return importingClassMetadata.getClassName() + "#" + PluginScannerRegistrar.class.getSimpleName() + "#" + index;
	}

}