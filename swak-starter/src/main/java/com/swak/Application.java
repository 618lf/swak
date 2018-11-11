package com.swak;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.ClassUtils;

import com.swak.reactivex.context.ReactiveServerApplicationContext;

/**
 * @ComponentScan("com.swak")
 * 
 * @ApplicationBoot
 * @author lifeng
 */
public class Application extends SpringApplication {

	/**
	 * 系统启动的日志
	 */
	public final static Logger APP_LOGGER = LoggerFactory.getLogger(Application.class);

	/**
	 * 是否是 WEB 环境
	 */
	private static final String REACTIVE_FLUX_ENVIRONMENT_CLASS = "com.swak.reactivex.transport.http.server.ReactiveServer";
	private static final String REACTIVE_VERT_ENVIRONMENT_CLASS = "com.swak.vertx.transport.ReactiveServer";

	/**
	 * 全局的 context
	 */
	private static ConfigurableApplicationContext applicationContext;

	/**
	 * 初始化
	 */
	public Application(Class<?>... primarySources) {
		super(primarySources);

		// 重新识别配置
		this.setWebApplicationType(this.deduceWebApplicationType());
	}

	/**
	 * 自动发现环境
	 * 
	 * @return
	 */
	private WebApplicationType deduceWebApplicationType() {
		if (ClassUtils.isPresent(REACTIVE_FLUX_ENVIRONMENT_CLASS, null)
				|| ClassUtils.isPresent(REACTIVE_VERT_ENVIRONMENT_CLASS, null)) {
			return WebApplicationType.REACTIVE;
		}
		return WebApplicationType.NONE;
	}

	/**
	 * 直接初始化这个context
	 */
	@Override
	protected ConfigurableApplicationContext createApplicationContext() {
		WebApplicationType type = this.getWebApplicationType();
		if (type == WebApplicationType.REACTIVE) {
			return (ConfigurableApplicationContext) BeanUtils.instantiateClass(ReactiveServerApplicationContext.class);
		}
		return (ConfigurableApplicationContext) BeanUtils.instantiateClass(AnnotationConfigApplicationContext.class);
	}

	/**
	 * 启动服务
	 * 
	 * @param primarySource
	 * @param args
	 * @return
	 */
	public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
		long start = System.currentTimeMillis();
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) new Application(primarySource)
				.run(args);
		long end = System.currentTimeMillis();
		if (context instanceof ReactiveServerApplicationContext) {
			APP_LOGGER.debug("Server start success in " + (end - start) / 1000 + "s" + ", listening on the port["
					+ ((ReactiveServerApplicationContext) context).getServer().getAddresses() + "]");
		} else {
			APP_LOGGER.debug("Server start success in " + (end - start) / 1000 + "s");
		}
		// 存储此context
		applicationContext = context;
		return context;
	}

	/**
	 * 停止服务
	 */
	public static void stop() {
		if (applicationContext != null) {
			applicationContext.close();
			applicationContext = null;
		}
	}
}