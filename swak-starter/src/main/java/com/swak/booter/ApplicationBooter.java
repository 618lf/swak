package com.swak.booter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import com.swak.reactivex.context.ReactiveServerApplicationContext;
import com.swak.reactivex.context.ReactiveServerInitializedEvent;

/**
 * 适配系统的启动： 適用web環境和非web環境
 * 
 * @author lifeng
 */
public interface ApplicationBooter extends ApplicationListener<ApplicationContextEvent> {
	
    /**
     * 判断是否服务器的器的启动环境
     */
	default void onApplicationEvent(ApplicationContextEvent event) {
		// 服务器启动后发布的事件
		if (event instanceof ReactiveServerInitializedEvent || (event instanceof ContextRefreshedEvent
				&& !(event.getSource() instanceof ReactiveServerApplicationContext))) {
			this.onApplicationEvent(event.getApplicationContext());
		}
	}
	
	/**
	 * 注意： <br>
	 * 1. 如果是服务器环境则需要等待服务器启动之后再初始化其他组件<br>
	 * 2. 如果是应用环境则可以直接启动<br>
	 * 
	 * @param context
	 */
	void onApplicationEvent(ApplicationContext context);
}
