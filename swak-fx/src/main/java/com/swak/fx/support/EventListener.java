package com.swak.fx.support;

import com.google.common.eventbus.Subscribe;

/**
 * Event 监听
 * 
 * @author lifeng
 */
public interface EventListener {

	/**
	 * 执行监听
	 * 
	 * @param event
	 */
	@Subscribe
	void listen(Event event);
}