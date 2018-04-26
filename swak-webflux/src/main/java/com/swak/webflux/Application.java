package com.swak.webflux;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.swak.webflux.config.ApplicationBoot;

/**
 * 自定义需要启动的类
 * @author lifeng
 */
@ComponentScan("com.swak")
@ApplicationBoot
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}