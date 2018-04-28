package com.swak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.swak.reactivex.context.ReactiveWebServerApplicationContext;

/**
 * @ComponentScan("com.swak")
 * @ApplicationBoot
 * @author lifeng
 */
public class Application extends SpringApplication {
	
	private final static Logger logger = LoggerFactory.getLogger(Application.class);
	
	/**
	 * 初始化
	 */
	public Application(Class<?>... primarySources) {
		super(primarySources);
		this.initApplication();
	}
	
	/**
	 * 取待 application
	 */
	public void initApplication() {
		this.setApplicationContextClass(ReactiveWebServerApplicationContext.class);
	}
	
	/**
	 * 启动服务
	 * @param primarySource
	 * @param args
	 * @return
	 */
	public static ConfigurableApplicationContext run(Class<?> primarySource,
			String... args) {
		ConfigurableApplicationContext context = new Application(primarySource).run(args);
		logger.debug("application is start success");
		return context;
	}
}