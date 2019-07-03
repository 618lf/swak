package com.swak.booter;

import static com.swak.Application.APP_LOGGER;

import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import com.swak.boot.Boot;
import com.swak.closable.ShutDownHook;

/**
 * 系统关闭， 資源清理
 * 
 * @author lifeng
 */
public class AppShuter implements ApplicationListener<ContextClosedEvent> {

	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		String[] boots = context.getBeanNamesForType(Boot.class);
		if (boots != null && boots.length > 0) {
			APP_LOGGER.debug("======== system startup Destorying ========");
			Arrays.stream(boots).forEach(s -> {
				Boot boot = context.getBean(s, Boot.class);
				APP_LOGGER.debug("Sync destory - {}", boot.describe());
				boot.destory();
			});
			APP_LOGGER.debug("======== system startup Destoryed  ========");
		}
		
		// 同步关闭资源
		ShutDownHook.runHook(true);
	}
}