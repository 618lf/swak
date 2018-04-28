package com.swak;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.swak.config.ApplicationBoot;
import com.swak.reactivex.context.ReactiveWebServerApplicationContext;

/**
 * 自定义需要启动的类
 * @author lifeng
 */
@ComponentScan("com.swak")
@ApplicationBoot
public class Application extends SpringApplication {
	
	/**
	 * 初始化
	 */
	public Application(Class<?>... primarySources) {
		super(primarySources);
		this.initApplication();
	}
	
	/**
	 * 执行设置需要启动的类
	 */
	public void initApplication() {
		this.setApplicationContextClass(ReactiveWebServerApplicationContext.class);
	}
	
	public static void main(String[] args) {
		new Application(Application.class).run(args);
    }
}