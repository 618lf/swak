package com.swak;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import com.swak.reactivex.context.EndPoints;
import com.swak.reactivex.context.ReactiveServerApplicationContext;
import com.swak.reactivex.context.Server;
import com.swak.utils.Sets;

/**
 * 代理启动 服务器环境
 *
 * @author: lifeng
 * @date: 2020/4/1 12:30
 */
public class Application extends SpringApplication {

	/**
	 * 系统启动的日志
	 */
	public final static Logger APP_LOGGER = LoggerFactory.getLogger(Application.class);

	/**
	 * 是否是 WEB 环境
	 */
	private static final String REACTIVE_FLUX_ENVIRONMENT_CLASS = "com.swak.flux.transport.server.ReactiveServer";
	private static final String REACTIVE_VERT_ENVIRONMENT_CLASS = "com.swak.vertx.transport.server.ReactiveServer";

	/**
	 * 全局的 context
	 */
	private static Application ME;
	private static ConfigurableApplicationContext CONTEXT;

	/**
	 * 初始化
	 */
	public Application(Class<?>... primarySources) {
		// 默认的扫描
		super(primarySources);

		// 重新识别配置
		this.setWebApplicationType(this.deduceWebApplicationType());

		// 指向唯一
		ME = this;
	}

	/**
	 * 自动发现环境
	 *
	 * @return WebApplicationType
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
			return BeanUtils.instantiateClass(ReactiveServerApplicationContext.class);
		}
		return BeanUtils.instantiateClass(AnnotationConfigApplicationContext.class);
	}

	/**
	 * 启动服务
	 *
	 * @param primarySource 代码入口
	 * @param args          参数
	 * @return 上下文
	 */
	public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
		long start = System.currentTimeMillis();
		CONTEXT = new Application(primarySource).run(args);
		long end = System.currentTimeMillis();
		if (CONTEXT instanceof ReactiveServerApplicationContext) {
			APP_LOGGER.debug("Server start success in " + (end - start) / 1000 + "s" + ", listening on ["
					+ ((ReactiveServerApplicationContext) CONTEXT).getServer().getEndPoints().toString() + "]");
		} else {
			APP_LOGGER.debug("Server start success in " + (end - start) / 1000 + "s");
		}
		return CONTEXT;
	}

	/**
	 * 返回发布的地址
	 *
	 * @return 地址
	 */
	public static EndPoints getEndPoints() {
		if (CONTEXT instanceof ReactiveServerApplicationContext) {

			// 获取服务
			Server server = ((ReactiveServerApplicationContext) CONTEXT).getServer();

			// 服务地址
			return server.getEndPoints();
		}

		// 其他的情况再说
		return null;
	}

	/**
	 * 停止服务
	 */
	public static void stop() {
		if (CONTEXT != null) {
			exit(CONTEXT);
			CONTEXT = null;
			ME = null;
		}
	}

	/**
	 * 当前应用
	 *
	 * @return Application
	 */
	public static Application me() {
		return ME;
	}

	/**
	 * 返回扫描的包
	 *
	 * @return 扫描的包
	 */
	public static Set<String> getScanPackages() {
		Set<String> packages = Sets.newHashSet();
		Set<Object> sources = Application.me().getAllSources();
		if (sources != null && !sources.isEmpty()) {
			for (Object source : sources) {
				if (source instanceof Class<?>) {
					Class<?> sourceClass = ((Class<?>) source);
					packages.addAll(parseComponent(sourceClass));
				}
			}
		}
		return packages;
	}

	/**
	 * 处理启动类
	 *
	 * @param sourceClass 源码
	 * @return 扫描的包
	 */
	@SuppressWarnings("deprecation")
	static Set<String> parseComponent(Class<?> sourceClass) {
		Set<String> packages = Sets.newHashSet();
		Set<ComponentScan> scans = AnnotationUtils.getRepeatableAnnotations(sourceClass, ComponentScan.class);
		for (ComponentScan scan : scans) {
			packages.addAll(parseComponentScan(sourceClass, scan));
		}

		// 扫描引入的包
		Set<Import> imports = AnnotationUtils.getRepeatableAnnotations(sourceClass, Import.class);
		for (Import scan : imports) {
			packages.addAll(parseComponentImport(sourceClass, scan));
		}
		return packages;
	}

	/**
	 * 简单的处理扫描的包
	 *
	 * @param sourceClass 源码入口
	 * @param scan        扫码器
	 * @return 扫描的包
	 */
	static Set<String> parseComponentScan(Class<?> sourceClass, ComponentScan scan) {
		String[] packages = scan.basePackages();
		if (packages.length == 0) {
			return Sets.newHashSet(sourceClass.getPackage().getName());
		}
		return Sets.newHashSet(packages);
	}

	/**
	 * 简单的处理引入的包
	 *
	 * @param sourceClass 源码入口
	 * @param scan        引入路径
	 * @return 扫描的包
	 */
	static Set<String> parseComponentImport(Class<?> sourceClass, Import scan) {
		Set<String> packages = Sets.newHashSet();
		Class<?>[] imports = scan.value();
		if (imports.length > 0) {
			for (Class<?> source : imports) {
				packages.addAll(parseComponent(source));
			}
		}
		return packages;
	}
}