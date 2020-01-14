package com.swak;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import com.swak.incrementer.IdGen;
import com.swak.serializer.SerializationUtils;

/**
 * 全局 App 对象
 * 
 * @author lifeng
 */
public final class App {

	private static App _ME = null;
	private ApplicationContext context = null;
	private String version = "1.0.0_final";
	private String serverSn = "server-1-1";
	private String serialization = "kryo_pool";

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

	public String getSerialization() {
		return serialization;
	}

	public App setSerialization(String serialization) {
		this.serialization = serialization;
		return this;
	}

	/**
	 * 初始化
	 */
	public App build() {
		IdGen.setServerSn(serverSn);
		SerializationUtils.setSerializer(serialization);

		// 返回当前对象
		_ME = this;
		return this;
	}

	/**
	 * 当前App
	 * 
	 * @return
	 */
	public static App me() {
		return _ME;
	}

	/**
	 * 获取 Bean
	 * 
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		try {
			return (T) _ME.context.getBean(name);
		} catch (Exception e) {
		}
		return null;
	}

	public static <T> T getBean(Class<T> requiredType) {
		try {
			return (T) _ME.context.getBean(requiredType);
		} catch (Exception e) {
		}
		return null;
	}

	public static <T> Map<String, T> getBeans(Class<T> type) {
		return _ME.context.getBeansOfType(type);
	}

	/**
	 * 获取资源文件
	 * 
	 * classpath:localtion file:localtion
	 * 
	 * @param localtion
	 * @return
	 */
	public static Resource resource(String localtion) {
		return _ME.context.getResource(localtion);
	}
}