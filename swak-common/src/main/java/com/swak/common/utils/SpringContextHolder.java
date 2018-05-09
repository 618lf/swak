package com.swak.common.utils; 

import java.util.Map;

import org.springframework.context.ApplicationContext;

/**
 * 以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候取出ApplicaitonContext.
 * @author lifeng
 */
public class SpringContextHolder {

	protected static ApplicationContext applicationContext = null;

	public static void setApplicationContext(ApplicationContext context) {
		applicationContext = context;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}
	
	/**
	 * 获得Bean 如果找不到则不抛出错误
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBeanQuietly(String name) {
		try {
			return (T) applicationContext.getBean(name);
		}catch(Exception e) {}
		return null;
	}
	
	public static <T> T getBeanQuietly(Class<T> requiredType) {
		try {
			return (T) applicationContext.getBean(requiredType);
		}catch(Exception e) {}
		return null;
	}
	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}
	
	public static <T> T getBean(String name, Class<T> type) {
		return applicationContext.getBean(name, type);
	}
	
	public static <T> Map<String,T> getBeans(Class<T> type) {
		return applicationContext.getBeansOfType(type);
	}
}