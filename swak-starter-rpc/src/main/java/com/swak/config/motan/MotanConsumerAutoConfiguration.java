package com.swak.config.motan;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.motan.properties.AnnotationBeanConfigProperties;
import com.swak.motan.properties.BasicServiceConfigProperties;
import com.swak.motan.properties.ProtocolConfigProperties;
import com.swak.motan.properties.RegistryConfigProperties;
import com.weibo.api.motan.config.BasicRefererInterfaceConfig;
import com.weibo.api.motan.config.ExtConfig;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RegistryConfig;
import com.weibo.api.motan.config.springsupport.RefererConfigBean;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;
import com.weibo.api.motan.config.springsupport.util.SpringBeanUtil;
import com.weibo.api.motan.util.LoggerUtil;

/**
 * Motan 配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ BasicServiceConfigProperties.class })
@AutoConfigureAfter(MotanAutoConfiguration.class)
@EnableConfigurationProperties({ AnnotationBeanConfigProperties.class, BasicServiceConfigProperties.class,
		ProtocolConfigProperties.class, RegistryConfigProperties.class })
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableMotan", matchIfMissing = true)
public class MotanConsumerAutoConfiguration implements DisposableBean {

	@Autowired
	private ApplicationContext applicationContext;
	@SuppressWarnings("rawtypes")
	private final ConcurrentMap<String, RefererConfigBean> referenceConfigs = new ConcurrentHashMap<String, RefererConfigBean>();

	@Bean
	public BeanPostProcessor beanPostProcessor() {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
				Class<?> clazz = bean.getClass();
				if (isProxyBean(bean)) {
					clazz = AopUtils.getTargetClass(bean);
				}
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					String name = method.getName();
					if (name.length() > 3 && name.startsWith("set") && method.getParameterTypes().length == 1
							&& Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers())) {
						try {
							MotanReferer reference = method.getAnnotation(MotanReferer.class);
							if (reference != null) {
								Object value = refer(reference, method.getParameterTypes()[0]);
								if (value != null) {
									method.invoke(bean, new Object[] { value });
								}
							}
						} catch (Exception e) {
							throw new BeanInitializationException("Failed to init remote service reference at method "
									+ name + " in class " + bean.getClass().getName(), e);
						}
					}
				}

				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					try {
						if (!field.isAccessible()) {
							field.setAccessible(true);
						}
						MotanReferer reference = field.getAnnotation(MotanReferer.class);
						if (reference != null) {
							Object value = refer(reference, field.getType());
							if (value != null) {
								field.set(bean, value);
							}
						}
					} catch (Exception e) {
						throw new BeanInitializationException("Failed to init remote service reference at filed "
								+ field.getName() + " in class " + bean.getClass().getName(), e);
					}
				}
				return bean;
			}
		};
	}

	/**
	 * refer proxy
	 *
	 * @param reference
	 * @param referenceClass
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T> Object refer(MotanReferer reference, Class<?> referenceClass) {
		String interfaceName;
		if (!void.class.equals(reference.interfaceClass())) {
			interfaceName = reference.interfaceClass().getName();
		} else if (referenceClass.isInterface()) {
			interfaceName = referenceClass.getName();
		} else {
			throw new IllegalStateException(
					"The @Reference undefined interfaceClass or interfaceName, and the property type "
							+ referenceClass.getName() + " is not a interface.");
		}
		String key = reference.group() + "/" + interfaceName + ":" + reference.version();
		RefererConfigBean<T> referenceConfig = referenceConfigs.get(key);
		if (referenceConfig == null) {
			referenceConfig = new RefererConfigBean<T>();
			referenceConfig.setBeanFactory(applicationContext);
			if (void.class.equals(reference.interfaceClass()) && referenceClass.isInterface()) {
				referenceConfig.setInterface((Class<T>) referenceClass);
			} else if (!void.class.equals(reference.interfaceClass())) {
				referenceConfig.setInterface((Class<T>) reference.interfaceClass());
			}

			if (applicationContext != null) {
				if (reference.protocol() != null && reference.protocol().length() > 0) {
					// 多个PROTOCOL
					List<ProtocolConfig> protocolConfigs = SpringBeanUtil.getMultiBeans(applicationContext,
							reference.protocol(), SpringBeanUtil.COMMA_SPLIT_PATTERN, ProtocolConfig.class);
					referenceConfig.setProtocols(protocolConfigs);
				}

				if (reference.directUrl() != null && reference.directUrl().length() > 0) {
					referenceConfig.setDirectUrl(reference.directUrl());
				}

				if (reference.basicReferer() != null && reference.basicReferer().length() > 0) {
					BasicRefererInterfaceConfig biConfig = applicationContext.getBean(reference.basicReferer(),
							BasicRefererInterfaceConfig.class);
					if (biConfig != null) {
						referenceConfig.setBasicReferer(biConfig);
					}
				}

				if (reference.client() != null && reference.client().length() > 0) {
					// TODO?
					// referenceConfig.setC(reference.client());
				}

				// String[] methods() default {};

				if (reference.registry() != null && reference.registry().length() > 0) {
					List<RegistryConfig> registryConfigs = SpringBeanUtil.getMultiBeans(applicationContext,
							reference.registry(), SpringBeanUtil.COMMA_SPLIT_PATTERN, RegistryConfig.class);
					referenceConfig.setRegistries(registryConfigs);
				}

				if (reference.extConfig() != null && reference.extConfig().length() > 0) {
					referenceConfig.setExtConfig(applicationContext.getBean(reference.extConfig(), ExtConfig.class));
				}

				if (reference.application() != null && reference.application().length() > 0) {
					referenceConfig.setApplication(reference.application());
				}
				if (reference.module() != null && reference.module().length() > 0) {
					referenceConfig.setModule(reference.module());
				}
				if (reference.group() != null && reference.group().length() > 0) {
					referenceConfig.setGroup(reference.group());
				}

				if (reference.version() != null && reference.version().length() > 0) {
					referenceConfig.setVersion(reference.version());
				}

				if (reference.proxy() != null && reference.proxy().length() > 0) {
					referenceConfig.setProxy(reference.proxy());
				}

				if (reference.filter() != null && reference.filter().length() > 0) {
					referenceConfig.setFilter(reference.filter());
				}

				if (reference.actives() > 0) {
					referenceConfig.setActives(reference.actives());
				}

				if (reference.async()) {
					referenceConfig.setAsync(reference.async());
				}

				if (reference.mock() != null && reference.mock().length() > 0) {
					referenceConfig.setMock(reference.mock());
				}

				if (reference.shareChannel()) {
					referenceConfig.setShareChannel(reference.shareChannel());
				}

				// if throw exception when call failure，the default value is ture
				if (reference.throwException()) {
					referenceConfig.setThrowException(reference.throwException());
				}
				if (reference.requestTimeout() > 0) {
					referenceConfig.setRequestTimeout(reference.requestTimeout());
				}
				if (reference.register()) {
					referenceConfig.setRegister(reference.register());
				}
				if (reference.accessLog()) {
					referenceConfig.setAccessLog("true");
				}
				if (reference.check()) {
					referenceConfig.setCheck("true");
				}
				if (reference.retries() > 0) {
					referenceConfig.setRetries(reference.retries());
				}
				if (reference.usegz()) {
					referenceConfig.setUsegz(reference.usegz());
				}
				if (reference.mingzSize() > 0) {
					referenceConfig.setMingzSize(reference.mingzSize());
				}
				if (reference.codec() != null && reference.codec().length() > 0) {
					referenceConfig.setCodec(reference.codec());
				}

				if (reference.mean() != null && reference.mean().length() > 0) {
					referenceConfig.setMean(reference.mean());
				}
				if (reference.p90() != null && reference.p90().length() > 0) {
					referenceConfig.setP90(reference.p90());
				}
				if (reference.p99() != null && reference.p99().length() > 0) {
					referenceConfig.setP99(reference.p99());
				}
				if (reference.p999() != null && reference.p999().length() > 0) {
					referenceConfig.setP999(reference.p999());
				}
				if (reference.errorRate() != null && reference.errorRate().length() > 0) {
					referenceConfig.setErrorRate(reference.errorRate());
				}

				try {
					referenceConfig.afterPropertiesSet();
				} catch (RuntimeException e) {
					throw (RuntimeException) e;
				} catch (Exception e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			}
			referenceConfigs.putIfAbsent(key, referenceConfig);
			referenceConfig = referenceConfigs.get(key);
		}

		return referenceConfig.getRef();
	}

	private boolean isProxyBean(Object bean) {
		return AopUtils.isAopProxy(bean);
	}

	/**
	 * release service/reference
	 *
	 * @throws Exception
	 */
	public void destroy() throws Exception {
		for (RefererConfigBean<?> referenceConfig : referenceConfigs.values()) {
			try {
				referenceConfig.destroy();
			} catch (Throwable e) {
				LoggerUtil.error(e.getMessage(), e);
			}
		}
	}
}
