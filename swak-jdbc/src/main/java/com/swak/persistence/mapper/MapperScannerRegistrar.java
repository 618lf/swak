package com.swak.persistence.mapper;

import java.lang.annotation.Annotation;
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
 * 自动注解查找的类
 * 
 * @author lifeng
 * @date 2020年4月13日 下午9:51:51
 */
public class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes mapperScanAttrs = AnnotationAttributes
				.fromMap(importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName()));
		if (mapperScanAttrs != null) {
			registerBeanDefinitions(importingClassMetadata, mapperScanAttrs, registry,
					generateBaseBeanName(importingClassMetadata, 0));
		}
	}

	void registerBeanDefinitions(AnnotationMetadata annoMeta, AnnotationAttributes annoAttrs,
			BeanDefinitionRegistry registry, String beanName) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
		Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
		if (!Annotation.class.equals(annotationClass)) {
			builder.addPropertyValue("annotationClass", annotationClass);
		}
		String sqlSessionTemplateRef = annoAttrs.getString("sqlSessionTemplateRef");
		if (StringUtils.hasText(sqlSessionTemplateRef)) {
			builder.addPropertyValue("sqlSessionTemplateBeanName", annoAttrs.getString("sqlSessionTemplateRef"));
		}
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
		return importingClassMetadata.getClassName() + "#" + MapperScannerRegistrar.class.getSimpleName() + "#" + index;
	}
}