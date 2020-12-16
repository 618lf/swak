package com.swak.config.vertx;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;

import com.swak.annotation.FluxService;
import com.swak.annotation.RestService;
import com.swak.vertx.config.ServiceBean;

/**
 * 处理服务Bean，且需要定义最后处理
 * 
 * @author lifeng
 * @date 2020年8月23日 下午9:35:04
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class FluxServiceAnnotationBeanPostProcessor implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

	private Environment environment;

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
			Class<?> beanClass = null;
			if (beanDefinition instanceof AbstractBeanDefinition
					&& ((AbstractBeanDefinition) beanDefinition).hasBeanClass()
					&& (beanClass = ((AbstractBeanDefinition) beanDefinition).getBeanClass()) != null
					&& this.isServie(beanClass)) {
				this.registerServiceBeanDefinition((DefaultListableBeanFactory) beanFactory, beanDefinitionName,
						beanClass);
			}
		}
	}

	private void registerServiceBeanDefinition(DefaultListableBeanFactory beanFactory, String beanDefinitionName,
			Class<?> beanClass) {
		FluxService mapping = AnnotatedElementUtils.findMergedAnnotation(beanClass, FluxService.class);

		Class<?>[] classes = ClassUtils.getAllInterfacesForClass(beanClass);
		if (classes.length == 0) {
			classes = new Class<?>[] { beanClass };
		}
		for (Class<?> inter : classes) {
			if (inter.getName().startsWith("org.springframework.") || !fitWith(mapping, inter)) {
				continue;
			}
			AbstractBeanDefinition serviceBeanDefinition = this.registryBeans(beanClass, inter, beanDefinitionName);
			beanFactory.registerBeanDefinition(this.generateServiceBeanName(beanDefinitionName, inter),
					serviceBeanDefinition);
		}
	}

	private String generateServiceBeanName(String beanName, Class<?> interfaceClass) {
		return new StringBuilder("@FluxService").append(" ").append(interfaceClass.getName()).toString();
	}

	private AbstractBeanDefinition registryBeans(Class<?> beanClass, Class<?> interClass, String beanName) {
		BeanDefinitionBuilder builder = rootBeanDefinition(ServiceBean.class);
		String resolvedBeanName = environment.resolvePlaceholders(beanName);
		builder.addPropertyReference("ref", resolvedBeanName);
		builder.addPropertyValue("beanClass", beanClass);
		builder.addPropertyValue("interClass", interClass);
		return builder.getBeanDefinition();
	}

	private boolean fitWith(FluxService mapping, Class<?> inter) {
		if (mapping.service() == void.class) {
			return true;
		}
		return mapping.service() == inter;
	}

	protected boolean isServie(Class<?> clazz) {
		return (clazz.isAnnotationPresent(FluxService.class) || clazz.isAnnotationPresent(RestService.class));
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
	}
}