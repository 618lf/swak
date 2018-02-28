package com.swak.common.boot;

/**
 * 系统启动项
 * @author root
 */
public interface Boot {

	/**
	 * 系统启动项 -- 启动
	 */
	public void start();
	
	/**
	 * 启动描述
	 * @return
	 */
	public String describe();
}