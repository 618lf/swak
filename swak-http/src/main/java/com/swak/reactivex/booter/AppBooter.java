package com.swak.reactivex.booter;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.swak.common.boot.Boot;

/**
 * 系统启动后需要做的工作
 * 
 * @author lifeng
 */
public class AppBooter implements ApplicationListener<ContextRefreshedEvent> {

	protected static Logger logger = LoggerFactory.getLogger(AppBooter.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();

		// 启动项
		logger.debug("========= system startup loading ====================");
		String[] boots = context.getBeanNamesForType(Boot.class);
		Arrays.stream(boots).forEach(s -> {
			Boot boot = context.getBean(s, Boot.class);
			logger.debug("Async loading - {}", boot.describe());
			boot.start();
		});
		logger.debug("========= system startup loaded ====================");
	}
}