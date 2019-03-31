package com.swak.flux.verticle;

/**
 * 模拟 vertx 的实现，但是不需要那么麻烦，所有的切换环节通过com来驱动
 * 
 * @author lifeng
 */
public interface Verticle {
	
	
	/**
	 * 发布ID
	 * 
	 * @return
	 */
	String address();
	
	/**
	 * 处理消息
	 * 
	 * @param event
	 */
	Msg handle(Msg event);
}