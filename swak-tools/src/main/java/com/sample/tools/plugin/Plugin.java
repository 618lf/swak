package com.sample.tools.plugin;

import javafx.scene.image.Image;

/**
 * 插件
 * 
 * @author lifeng
 * @date 2020年6月2日 下午3:32:59
 */
public interface Plugin {

	/**
	 * 图标
	 * 
	 * @return
	 */
	Image logo();

	/**
	 * 文字
	 * 
	 * @return
	 */
	String text();

	/**
	 * 事件
	 */
	void action();
}