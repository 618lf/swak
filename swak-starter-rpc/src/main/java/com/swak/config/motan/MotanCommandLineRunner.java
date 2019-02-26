package com.swak.config.motan;

import javax.annotation.Resource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

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
public class MotanCommandLineRunner implements CommandLineRunner {

	/** Registry Config */
	@Resource
	private RegistryConfigProperties registryConfig;

	@Override
	public void run(String... args) throws Exception {
		if (registryConfig.getRegProtocol() != null && !registryConfig.getRegProtocol().toLowerCase().equals("local")) {
			// 开启注册中心
			MotanSwitcherUtil.setSwitcherValue(MotanConstants.REGISTRY_HEARTBEAT_SWITCHER, true);
		}
	}
}
