package com.swak.manage.widgets;

/**
 * 窗口
 * 
 * @author lifeng
 */
public interface Window {

	/**
	 * 设置主题
	 * 
	 * @param me
	 */
	void theme(Theme me);
	
	/**
	 * 展示页面
	 */
	void open();
}