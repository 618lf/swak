package com.swak.config.vertx;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import java.lang.reflect.Method;

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

import com.swak.annotation.RequestMapping;
import com.swak.annotation.RestApi;
import com.swak.annotation.RestPage;
import com.swak.annotation.RestService;
import com.swak.utils.ReflectUtils;
import com.swak.vertx.config.RouterBean;

/**
 * 定义路由处理
 * 
 * @author lifeng
 * @date 2020年8月23日 下午9:37:46
 */
@Order(Ordered.LOWEST_PRECEDENCE)
public class RouterAnnotationBeanProcessor implements EnvironmentAware, BeanDefinitionRegistryPostProcessor {

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
					&& this.isController(beanClass)) {
				this.registerServiceBeanDefinition((DefaultListableBeanFactory) beanFactory, beanDefinitionName,
						beanClass);
			}
		}
	}

	private void registerServiceBeanDefinition(DefaultListableBeanFactory beanFactory, String beanDefinitionName,
			Class<?> beanClass) {
		Method[] methods = beanClass.getDeclaredMethods();
		for (Method method : methods) {
			RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
			if (methodMapping == null) {
				continue;
			}

			AbstractBeanDefinition serviceBeanDefinition = this.registryBeans(beanClass, beanDefinitionName, method);
			beanFactory.registerBeanDefinition(generateServiceBeanName(beanClass, method), serviceBeanDefinition);
		}
	}

	private String generateServiceBeanName(Class<?> interfaceClass, Method method) {
		return new StringBuilder("@RestApi").append(" ").append(interfaceClass.getName()).append(".")
				.append(ReflectUtils.getDesc(method)).toString();
	}

	/**
	 * 注册服务Bean
	 * 
	 * @param beanClass
	 * @param beanName
	 * @param mapping
	 */
	private AbstractBeanDefinition registryBeans(Class<?> beanClass, String beanName, Method method) {
		BeanDefinitionBuilder builder = rootBeanDefinition(RouterBean.class);
		String resolvedBeanName = environment.resolvePlaceholders(beanName);
		builder.addPropertyReference("ref", resolvedBeanName);
		builder.addPropertyValue("type", beanClass);
		builder.addPropertyValue("method", method);
		return builder.getBeanDefinition();
	}

	/**
	 * 是否是 Api
	 *
	 * @param clazz 类
	 * @return 是否是Api
	 */
	protected boolean isController(Class<?> clazz) {
		return (clazz.isAnnotationPresent(RestApi.class) || clazz.isAnnotationPresent(RestPage.class)
				|| clazz.isAnnotationPresent(RestService.class));
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
	}
}