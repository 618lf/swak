package com.swak.actuator.web;

import java.util.List;

import com.swak.utils.Lists;

/**
 * handler 的 描述
 * @author lifeng
 */
public class HandlersDescription {
	
	private final List<String> handlers;
	
	public HandlersDescription() {
		handlers = Lists.newArrayList();
	}
	
	/**
	 * add handler
	 * @param method
	 * @param paths
	 * @param handler
	 * @return
	 */
	public HandlersDescription addHandler(String description) {
		handlers.add(description);
		return this;
	}

	public List<String> getHandlers() {
		return handlers;
	}
}