package com.swak.config.motan;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.motan.properties.AnnotationBeanConfigProperties;
import com.swak.motan.properties.BasicServiceConfigProperties;
import com.swak.motan.properties.ProtocolConfigProperties;
import com.swak.motan.properties.RegistryConfigProperties;
import com.swak.utils.StringUtils;
import com.weibo.api.motan.config.BasicServiceInterfaceConfig;
import com.weibo.api.motan.config.ConfigUtil;
import com.weibo.api.motan.config.ExtConfig;
import com.weibo.api.motan.config.ProtocolConfig;
import com.weibo.api.motan.config.RegistryConfig;
import com.weibo.api.motan.config.springsupport.ServiceConfigBean;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;
import com.weibo.api.motan.config.springsupport.util.SpringBeanUtil;
import com.weibo.api.motan.util.ConcurrentHashSet;
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
public class MotanProviderAutoConfiguration implements DisposableBean {

	@Autowired
	private ApplicationContext applicationContext;
	private final Set<ServiceConfigBean<?>> serviceConfigs = new ConcurrentHashSet<ServiceConfigBean<?>>();

	@PostConstruct
	public void init() throws Exception {
		Map<String, Object> beanMap = this.applicationContext.getBeansWithAnnotation(MotanService.class);
		if (beanMap != null && beanMap.size() > 0) {
			for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
				this.initProviderBean(entry.getKey(), entry.getValue());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void initProviderBean(String beanName, Object bean) throws Exception {
		Class<?> clazz = bean.getClass();
		if (isProxyBean(bean)) {
			clazz = AopUtils.getTargetClass(bean);
		}
		MotanService service = clazz.getAnnotation(MotanService.class);
		if (service != null) {
			ServiceConfigBean<Object> serviceConfig = new ServiceConfigBean<Object>();
			if (void.class.equals(service.interfaceClass())) {
				if (clazz.getInterfaces().length > 0) {
					Class<Object> clz = (Class<Object>) clazz.getInterfaces()[0];
					serviceConfig.setInterface(clz);
				} else {
					throw new IllegalStateException("Failed to export remote service class " + clazz.getName()
							+ ", cause: The @Service undefined interfaceClass or interfaceName, and the service class unimplemented any interfaces.");
				}
			} else {
				serviceConfig.setInterface((Class<Object>) service.interfaceClass());
			}
			if (applicationContext != null) {

				serviceConfig.setBeanFactory(applicationContext);

				if (service.basicService() != null && service.basicService().length() > 0) {
					serviceConfig.setBasicService(
							applicationContext.getBean(service.basicService(), BasicServiceInterfaceConfig.class));
				}

				if (service.export() != null && service.export().length() > 0) {
					serviceConfig.setExport(service.export());
				}

				if (service.host() != null && service.host().length() > 0) {
					serviceConfig.setHost(service.host());
				}

				String protocolValue = null;
				if (service.protocol() != null && service.protocol().length() > 0) {
					protocolValue = service.protocol();
				} else if (service.export() != null && service.export().length() > 0) {
					protocolValue = ConfigUtil.extractProtocols(service.export());
				}

				if (!StringUtils.isBlank(protocolValue)) {
					List<ProtocolConfig> protocolConfigs = SpringBeanUtil.getMultiBeans(applicationContext,
							protocolValue, SpringBeanUtil.COMMA_SPLIT_PATTERN, ProtocolConfig.class);
					serviceConfig.setProtocols(protocolConfigs);
				}

				// String[] methods() default {};

				if (service.registry() != null && service.registry().length() > 0) {
					List<RegistryConfig> registryConfigs = SpringBeanUtil.getMultiBeans(applicationContext,
							service.registry(), SpringBeanUtil.COMMA_SPLIT_PATTERN, RegistryConfig.class);
					serviceConfig.setRegistries(registryConfigs);
				}

				if (service.extConfig() != null && service.extConfig().length() > 0) {
					serviceConfig.setExtConfig(applicationContext.getBean(service.extConfig(), ExtConfig.class));
				}

				if (service.application() != null && service.application().length() > 0) {
					serviceConfig.setApplication(service.application());
				}
				if (service.module() != null && service.module().length() > 0) {
					serviceConfig.setModule(service.module());
				}
				if (service.group() != null && service.group().length() > 0) {
					serviceConfig.setGroup(service.group());
				}

				if (service.version() != null && service.version().length() > 0) {
					serviceConfig.setVersion(service.version());
				}

				if (service.proxy() != null && service.proxy().length() > 0) {
					serviceConfig.setProxy(service.proxy());
				}

				if (service.filter() != null && service.filter().length() > 0) {
					serviceConfig.setFilter(service.filter());
				}

				if (service.actives() > 0) {
					serviceConfig.setActives(service.actives());
				}

				if (service.async()) {
					serviceConfig.setAsync(service.async());
				}

				if (service.mock() != null && service.mock().length() > 0) {
					serviceConfig.setMock(service.mock());
				}

				// 是否共享 channel
				if (service.shareChannel()) {
					serviceConfig.setShareChannel(service.shareChannel());
				}

				// if throw exception when call failure，the default value is ture
				if (service.throwException()) {
					serviceConfig.setThrowException(service.throwException());
				}
				if (service.requestTimeout() > 0) {
					serviceConfig.setRequestTimeout(service.requestTimeout());
				}
				if (service.register()) {
					serviceConfig.setRegister(service.register());
				}
				if (service.accessLog()) {
					serviceConfig.setAccessLog("true");
				}
				if (service.check()) {
					serviceConfig.setCheck("true");
				}
				if (service.usegz()) {
					serviceConfig.setUsegz(service.usegz());
				}

				if (service.retries() > 0) {
					serviceConfig.setRetries(service.retries());
				}

				if (service.mingzSize() > 0) {
					serviceConfig.setMingzSize(service.mingzSize());
				}

				if (service.codec() != null && service.codec().length() > 0) {
					serviceConfig.setCodec(service.codec());
				}

				try {
					serviceConfig.afterPropertiesSet();
				} catch (RuntimeException e) {
					throw (RuntimeException) e;
				} catch (Exception e) {
					throw new IllegalStateException(e.getMessage(), e);
				}
			}
			serviceConfig.setRef(bean);
			serviceConfigs.add(serviceConfig);
			serviceConfig.export();
		}
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
		for (ServiceConfigBean<?> serviceConfig : serviceConfigs) {
			try {
				serviceConfig.unexport();
			} catch (Throwable e) {
				LoggerUtil.error(e.getMessage(), e);
			}
		}
	}
}
