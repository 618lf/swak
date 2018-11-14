package com.swak.booter;

import static com.swak.Application.APP_LOGGER;

import java.util.Arrays;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.swak.boot.Boot;

/**
 * 系统启动后需要做的工作
 * 
 * @author lifeng
 */
public class AppBooter implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		String[] boots = context.getBeanNamesForType(Boot.class);
		if (boots != null && boots.length > 0) {
			APP_LOGGER.debug("========= system startup loading ====================");
			Arrays.stream(boots).forEach(s -> {
				Boot boot = context.getBean(s, Boot.class);
				APP_LOGGER.debug("Async loading - {}", boot.describe());
				boot.start();
			});
			APP_LOGGER.debug("========= system startup loaded ====================");
		}
	}
}