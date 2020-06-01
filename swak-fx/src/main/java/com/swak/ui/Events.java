package com.swak.ui;

import com.google.common.eventbus.EventBus;

/**
 * 时间
 * 
 * @author lifeng
 * @date 2020年5月24日 下午7:44:41
 */
public enum Events {

	INSTANCE;

	public static EventBus eventBus;

	/**
	 * 发送事件
	 * 
	 * @param event
	 */
	public static void post(Event event) {
		eventBus.post(event);
	}
}
