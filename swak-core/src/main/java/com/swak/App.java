package com.swak;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.swak.incrementer.IdGen;

/**
 * 全局 App 对象
 *
 * @author: lifeng
 * @date: 2020/3/29 15:39
 */
public final class App {

	private static App ME = null;
	private ApplicationContext context = null;
	private String version = "0.0.0";
	private String serverSn = "server-1-1";
	private OS os;

	public ApplicationContext getContext() {
		return context;
	}

	public App setContext(ApplicationContext context) {
		this.context = context;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public App setVersion(String version) {
		this.version = version;
		return this;
	}

	public String getServerSn() {
		return serverSn;
	}

	public App setServerSn(String serverSn) {
		this.serverSn = serverSn;
		return this;
	}

	public OS getOs() {
		return os;
	}

	/**
	 * 初始化
	 */
	public App build() {

		// 系统
		os = OS.me();

		// 本机信息初始化主键
		IdGen.setServerSn(serverSn);

		// 返回当前对象
		ME = this;
		return this;
	}

	/**
	 * 当前App
	 *
	 * @return APP
	 */
	public static App me() {
		return ME;
	}

	/**
	 * 获取 Bean
	 *
	 * @param name bean name
	 * @return Bean
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		try {
			return (T) ME.context.getBean(name);
		} catch (Exception ignored) {
		}
		return null;
	}

	/**
	 * 获取 Bean
	 *
	 * @param requiredType bean type
	 * @return Bean
	 */
	public static <T> T getBean(Class<T> requiredType) {
		try {
			return ME.context.getBean(requiredType);
		} catch (Exception ignored) {
		}
		return null;
	}

	/**
	 * 获取 Beans
	 *
	 * @param type bean type
	 * @return Beans
	 */
	public static <T> Map<String, T> getBeans(Class<T> type) {
		return ME.context.getBeansOfType(type);
	}

	/**
	 * 获取资源文件
	 * <p>
	 * classpath:localtion file:localtion
	 *
	 * @param localtion 资源路劲
	 * @return 资源
	 */
	public static Resource resource(String localtion) {
		return ME.context.getResource(localtion);
	}

	/**
	 * 获取资源文件 -- 以文件流的方式获取（可以获取jar中的资源）
	 *
	 * @param localtion 资源文件
	 * @return 流
	 */
	public static InputStream inputStream(String localtion) {
		try {
			Resource resource = App.resource(localtion);
			if (resource.isFile()) {
				return new FileInputStream(resource.getFile());
			}
			return resource.getInputStream();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 获取资源文件 -- 以文件的方式获取(不能获取jar中的资源)
	 *
	 * @param localtion 资源文件
	 * @return 流
	 */
	public static File file(String localtion) {
		try {
			Resource resource = App.resource(localtion);
			if (resource.isFile()) {
				return resource.getFile();
			}
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}