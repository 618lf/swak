package com.swak.vertx.security.handler;

/**
 * 参数配置
 * @author lifeng
 */
public interface PathDefinition {

	/**
	 * 路径配置
	 * 
	 * @param path
	 * @param param
	 */
	void pathConfig(String path, String param);
}