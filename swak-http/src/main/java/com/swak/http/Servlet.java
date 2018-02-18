package com.swak.http;

import org.springframework.context.ApplicationContext;

/**
 * Servlet
 * @author lifeng
 */
public interface Servlet {

	/**
	 * 初始化
	 */
	void init(ApplicationContext applicationContext);
	
	/**
	 * 执行服务
	 */
	void doService(HttpServletRequest request, HttpServletResponse response);
	
	/**
	 * 销毁
	 */
	void destroy();
}