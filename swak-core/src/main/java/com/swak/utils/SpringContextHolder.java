package com.swak.utils; 

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

/**
 * 以静态变量保存Spring ApplicationContext
 * @author lifeng
 */
public class SpringContextHolder {
	
	protected static ApplicationContext applicationContext = null;
	
	public static void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
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
			return (T) applicationContext.getBean(name);
		}catch(Exception e) {}
		return null;
	}
	public static <T> T getBean(Class<T> requiredType) {
		try {
			return (T) applicationContext.getBean(requiredType);
		}catch(Exception e) {}
		return null;
	}
	public static <T> Map<String,T> getBeans(Class<T> type) {
		return applicationContext.getBeansOfType(type);
	}
	
	/**
	 * 获取资源文件
	 * classpath:localtion
	 * file:localtion
	 * @param localtion
	 * @return
	 */
	public static Resource resource(String localtion) {
		return applicationContext.getResource(localtion);
	}
}