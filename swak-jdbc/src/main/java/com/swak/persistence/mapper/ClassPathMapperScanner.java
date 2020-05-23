package com.swak.persistence.mapper;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.StringUtils;

/**
 * 类扫描
 * 
 * @author lifeng
 * @date 2020年4月13日 下午9:52:38
 */
@SuppressWarnings("rawtypes")
public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {

	private Logger LOGGER = LoggerFactory.getLogger(ClassPathMapperScanner.class);

	private Class<? extends MapperFactoryBean> mapperFactoryBeanClass = MapperFactoryBean.class;
	private Class<? extends Annotation> annotationClass;
	private String sqlSessionTemplateBeanName;

	public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
		super(registry, false);
	}

	public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	public void setSqlSessionTemplateBeanName(String sqlSessionTemplateBeanName) {
		this.sqlSessionTemplateBeanName = sqlSessionTemplateBeanName;
	}

	/**
	 * Configures parent scanner to search for the right interfaces. It can search
	 * for all interfaces or just for those that extends a markerInterface or/and
	 * those annotated with the annotationClass
	 */
	public void registerFilters() {
		boolean acceptAllInterfaces = true;

		// if specified, use the given annotation
		if (this.annotationClass != null) {
			addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
			acceptAllInterfaces = false;
		}

		if (acceptAllInterfaces) {
			// default include filter that accepts all classes
			addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
		}

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
			if (StringUtils.hasText(this.sqlSessionTemplateBeanName)) {
				definition.getPropertyValues().add("sqlSessionTemplate",
						new RuntimeBeanReference(this.sqlSessionTemplateBeanName));
			} else {
				definition.getPropertyValues().add("sqlSessionTemplate",
						new RuntimeBeanReference(SqlSessionTemplate.class));
			}
			definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
		if (super.checkCandidate(beanName, beanDefinition)) {
			return true;
		} else {
			LOGGER.warn(
					"Skipping MapperFactoryBean with name '" + beanName + "' and '" + beanDefinition.getBeanClassName()
							+ "' mapperInterface" + ". Bean already defined with the same name!");
			return false;
		}
	}
}