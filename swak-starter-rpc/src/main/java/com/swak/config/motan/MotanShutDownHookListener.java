package com.swak.config.motan;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import com.weibo.api.motan.closable.ShutDownHook;

/**
 * 容器关闭
 * 
 * @author lifeng
 */
public class MotanShutDownHookListener implements ApplicationListener<ContextClosedEvent> {

	@Override
	public void onApplicationEvent(ContextClosedEvent arg0) {
		ShutDownHook.runHook(true);
	}
}