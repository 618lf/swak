package com.swak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.swak.reactivex.context.ReactiveServerApplicationContext;

/**
 * @ComponentScan("com.swak")
 * @ApplicationBoot
 * @author lifeng
 */
public class Application extends SpringApplication {
	
	/**
	 * 系统启动的日志
	 */
	public final static Logger APP_LOGGER = LoggerFactory.getLogger(Application.class);
	
	/**
	 * 初始化
	 */
	public Application(Class<?>... primarySources) {
		super(primarySources);
	}
	
	/**
	 * 直接初始化这个context
	 */
	@Override
	protected ConfigurableApplicationContext createApplicationContext() {
		return (ConfigurableApplicationContext) BeanUtils.instantiateClass(ReactiveServerApplicationContext.class);
	}

	/**
	 * 启动服务
	 * @param primarySource
	 * @param args
	 * @return
	 */
	public static ConfigurableApplicationContext run(Class<?> primarySource,
			String... args) {
		long start = System.currentTimeMillis();
		ConfigurableApplicationContext context = new Application(primarySource).run(args);
		long end = System.currentTimeMillis();
		APP_LOGGER.debug("Server start success in "  + (end - start)/ 1000 + "s");
		return context;
	}
}