package com.swak.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.App;
import com.swak.ApplicationProperties;
import com.swak.booter.AppBooter;
import com.swak.booter.AppShuter;

/**
 * 系统配置 - 启动和关闭
 * 
 * @author lifeng
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
	 * @return
	 */
	@Bean
	public App app() {
		return new App().setContext(context).setVersion(properties.getVersion()).setServerSn(properties.getServerSn())
				.setSerialization(properties.getSerialization()).build();
	}

	/**
	 * 启动
	 * 
	 * @return
	 */
	@Bean
	public AppBooter appBooter() {
		return new AppBooter();
	}

	/**
	 * 关闭
	 * 
	 * @return
	 */
	@Bean
	public AppShuter appShuter() {
		return new AppShuter();
	}
}