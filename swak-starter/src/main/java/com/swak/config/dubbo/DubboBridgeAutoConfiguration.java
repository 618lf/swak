package com.swak.config.dubbo;

import static org.apache.dubbo.spring.boot.util.DubboUtils.DUBBO_PREFIX;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * dubbo服务配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration.class)
@AutoConfigureAfter(org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration.class)
@ConditionalOnProperty(prefix = DUBBO_PREFIX, name = "enabled", matchIfMissing = true)
public class DubboBridgeAutoConfiguration {

	/**
	 * 开启dubbo 的桥接服务
	 * 
	 * @return
	 */
	public ReferenceBridgeAnnotationBeanPostProcessor referenceBridgeAnnotationBeanPostProcessor() {
		return new ReferenceBridgeAnnotationBeanPostProcessor();
	}
}