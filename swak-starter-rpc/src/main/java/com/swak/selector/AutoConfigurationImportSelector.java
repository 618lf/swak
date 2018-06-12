package com.swak.selector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.swak.ApplicationBoot;

/**
 * 自定义配置文件导入
 * 
 * @author lifeng
 */
public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware, Ordered {

	private ClassLoader beanClassLoader;

	@Override
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
		AnnotationAttributes attributes = getAttributes(annotationMetadata);
		List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
		configurations = removeDuplicates(configurations);
		return StringUtils.toStringArray(configurations);
	}

	@Override
	public Class<? extends Group> getImportGroup() {
		return AutoConfigurationGroup.class;
	}

	protected final <T> List<T> removeDuplicates(List<T> list) {
		return new ArrayList<>(new LinkedHashSet<>(list));
	}

	protected AnnotationAttributes getAttributes(AnnotationMetadata metadata) {
		String name = getAnnotationClass().getName();
		AnnotationAttributes attributes = AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(name, true));
		Assert.notNull(attributes, () -> "No auto-configuration attributes found. Is " + metadata.getClassName()
				+ " annotated with " + ClassUtils.getShortName(name) + "?");
		return attributes;
	}

	protected Class<?> getAnnotationClass() {
		return ApplicationBoot.class;
	}

	protected List<String> getCandidateConfigurations(AnnotationMetadata metadata, AnnotationAttributes attributes) {
		List<String> configurations = SwakFactoriesLoader.loadFactoryNames(getAnnotationClass(), getBeanClassLoader());
		Assert.notEmpty(configurations, "No auto configuration classes found in META-INF/swak.factories. If you "
				+ "are using a custom packaging, make sure that file is correct.");
		return configurations;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 1;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}

	protected ClassLoader getBeanClassLoader() {
		return this.beanClassLoader;
	}

	private static class AutoConfigurationGroup
			implements DeferredImportSelector.Group, BeanFactoryAware {

		private BeanFactory beanFactory;

		private final Map<String, AnnotationMetadata> entries = new LinkedHashMap<>();

		@Override
		public void setBeanFactory(BeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}

		@Override
		public void process(AnnotationMetadata annotationMetadata, DeferredImportSelector deferredImportSelector) {
			String[] imports = deferredImportSelector.selectImports(annotationMetadata);
			for (String importClassName : imports) {
				this.entries.put(importClassName, annotationMetadata);
			}
		}

		@Override
		public Iterable<Entry> selectImports() {
			return sortAutoConfigurations().stream()
					.map((importClassName) -> new Entry(this.entries.get(importClassName), importClassName))
					.collect(Collectors.toList());
		}

		private List<String> sortAutoConfigurations() {
			List<String> autoConfigurations = new ArrayList<>(this.entries.keySet());
			if (this.entries.size() <= 1) {
				return autoConfigurations;
			}
			return new AutoConfigurationSorter(getMetadataReaderFactory()).getInPriorityOrder(autoConfigurations);
		}

		private MetadataReaderFactory getMetadataReaderFactory() {
			try {
				String BEAN_NAME = "org.springframework.boot.autoconfigure.internalCachingMetadataReaderFactory";
				return this.beanFactory.getBean(BEAN_NAME, MetadataReaderFactory.class);
			} catch (NoSuchBeanDefinitionException ex) {
				throw ex;
			}
		}
	}
}