package com.swak.config.motan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

import com.swak.motan.properties.BasicServiceConfigProperties;
import com.swak.motan.properties.RegistryConfigProperties;
import com.weibo.api.motan.common.MotanConstants;
import com.weibo.api.motan.util.MotanSwitcherUtil;

/**
 * MotanCommandLineRunner
 * 
 * @author alanwei
 * @since 2016-09-11
 */
@Configuration
@ConditionalOnClass({ BasicServiceConfigProperties.class })
public class MotanCommandLineRunner implements CommandLineRunner {

	/** Registry Config */
	@Autowired
	private RegistryConfigProperties registryConfig;

	@Override
	public void run(String... args) throws Exception {
		if (registryConfig.getRegProtocol() != null && !registryConfig.getRegProtocol().toLowerCase().equals("local")) {
			// 开启注册中心
			MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
		}
	}
}
