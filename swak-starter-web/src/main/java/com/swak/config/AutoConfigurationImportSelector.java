package com.swak.config;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.swak.ApplicationBoot;

/**
 * 自定义配置文件导入
 * @author lifeng
 */
public class AutoConfigurationImportSelector implements DeferredImportSelector, BeanClassLoaderAware, Ordered {

	private ClassLoader beanClassLoader;
	
	@Override
	public String[] selectImports(AnnotationMetadata annotationMetadata) {
		AnnotationAttributes attributes = getAttributes(annotationMetadata);
		List<String> configurations = getCandidateConfigurations(annotationMetadata,
				attributes);
		configurations = removeDuplicates(configurations);
		return StringUtils.toStringArray(configurations);
	}
	
	protected final <T> List<T> removeDuplicates(List<T> list) {
		return new ArrayList<>(new LinkedHashSet<>(list));
	}
	
	/**
	 * Return the appropriate {@link AnnotationAttributes} from the
	 * {@link AnnotationMetadata}. By default this method will return attributes for
	 * {@link #getAnnotationClass()}.
	 * @param metadata the annotation metadata
	 * @return annotation attributes
	 */
	protected AnnotationAttributes getAttributes(AnnotationMetadata metadata) {
		String name = getAnnotationClass().getName();
		AnnotationAttributes attributes = AnnotationAttributes
				.fromMap(metadata.getAnnotationAttributes(name, true));
		Assert.notNull(attributes,
				() -> "No auto-configuration attributes found. Is "
						+ metadata.getClassName() + " annotated with "
						+ ClassUtils.getShortName(name) + "?");
		return attributes;
	}
	
	/**
	 * Return the source annotation class used by the selector.
	 * @return the annotation class
	 */
	protected Class<?> getAnnotationClass() {
		return ApplicationBoot.class;
	}
	
	/**
	 * Return the auto-configuration class names that should be considered. By default
	 * this method will load candidates using {@link SpringFactoriesLoader} with
	 * {@link #getSpringFactoriesLoaderFactoryClass()}.
	 * @param metadata the source metadata
	 * @param attributes the {@link #getAttributes(AnnotationMetadata) annotation
	 * attributes}
	 * @return a list of candidate configurations
	 */
	protected List<String> getCandidateConfigurations(AnnotationMetadata metadata,
			AnnotationAttributes attributes) {
		List<String> configurations = SwakFactoriesLoader.loadFactoryNames(
				getAnnotationClass(), getBeanClassLoader());
		Assert.notEmpty(configurations,
				"No auto configuration classes found in META-INF/swak.factories. If you "
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
}