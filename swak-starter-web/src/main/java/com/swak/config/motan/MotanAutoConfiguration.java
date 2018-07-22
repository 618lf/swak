package com.swak.config.motan;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.Constants;
import com.swak.motan.MotanProperties;
import com.weibo.api.motan.config.springsupport.AnnotationBean;
import com.weibo.api.motan.config.springsupport.BasicRefererConfigBean;
import com.weibo.api.motan.config.springsupport.ProtocolConfigBean;
import com.weibo.api.motan.config.springsupport.RegistryConfigBean;

/**
 * RPC 客户端的配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ MotanProperties.class })
@EnableConfigurationProperties(MotanProperties.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "enableMotan", matchIfMissing = true)
public class MotanAutoConfiguration {

	public MotanAutoConfiguration() {
		APP_LOGGER.debug("Loading Motan");
	}

	/**
	 * 扫描的包
	 * 
	 * @return
	 */
	@Bean
	public AnnotationBean motanAnnotationBean(MotanProperties motanProperties) {
		AnnotationBean motanAnnotationBean = new AnnotationBean();
		motanAnnotationBean.setPackage(motanProperties.getAnnotationPackage());
		return motanAnnotationBean;
	}

	/**
	 * 协议
	 * 
	 * @return
	 */
	@Bean(name = "motan")
	public ProtocolConfigBean protocolConfig(MotanProperties motanProperties) {
		ProtocolConfigBean config = new ProtocolConfigBean();
		config.setDefault(motanProperties.isProtocolDefault());
		config.setName(motanProperties.getProtocolName());
		config.setMaxContentLength(motanProperties.getProtocolMaxContentLength());
		return config;
	}

	/**
	 * 注册
	 * @return
	 */
	@Bean(name = "registry")
	public RegistryConfigBean registryConfig(MotanProperties motanProperties) {
		RegistryConfigBean config = new RegistryConfigBean();
		config.setRegProtocol(motanProperties.getRegistryProtocol());
		config.setAddress(motanProperties.getRegistryAddress());
		return config;
	}
	
	/**
	 * 依赖配置
	 * @return
	 */
    @Bean
    public BasicRefererConfigBean refererConfig(MotanProperties motanProperties) {
        BasicRefererConfigBean config = new BasicRefererConfigBean();
        config.setProtocol("motan");
        config.setGroup("motan-rpc");
        config.setModule("motan-rpc");
        config.setApplication("motan");
        config.setRegistry("registry");
        config.setCheck(motanProperties.isRefererCheck());
        config.setAccessLog(motanProperties.isRefererAccessLog());
        config.setRetries(motanProperties.getRefererRetries());
        config.setThrowException(motanProperties.isRefererThrowException());
        return config;
    }
}
