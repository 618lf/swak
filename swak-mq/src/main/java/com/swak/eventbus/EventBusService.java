package com.swak.eventbus;

/**
 * 消息服务 -- 服务消息应该继承此基准消息API
 * 
 * @author lifeng
 * @date 2020年10月11日 下午10:19:00
 */
public abstract class EventBusService {

	public void post(Object message) {
		EventBus.me().post(message);
	}
}