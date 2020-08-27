package com.swak.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.App;
import com.swak.ApplicationProperties;
import com.swak.Version;
import com.swak.booter.AppBooter;
import com.swak.booter.AppShuter;

/**
 * 系统配置 - 启动和关闭
 *
 * @author: lifeng
 * @date: 2020/4/1 12:38
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationAutoConfiguration {

	@Autowired
	private ApplicationContext context;
	@Autowired
	private ApplicationProperties properties;

	/**
	 * 系统
	 *
	 * @return APP
	 */
	@Bean
	public App app() {
		return new App().setContext(context).setVersion(Version.getVersion()).setServerSn(properties.getServerSn())
				.setSerialization(properties.getSerialization()).build();
	}

	/**
	 * 启动
	 *
	 * @return AppBooter
	 */
	@Bean
	public AppBooter appBooter() {
		return new AppBooter();
	}

	/**
	 * 关闭
	 *
	 * @return AppShuter
	 */
	@Bean
	public AppShuter appShuter() {
		return new AppShuter();
	}
}