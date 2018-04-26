package com.swak.vertx;

import org.springframework.boot.SpringApplication;

import com.swak.vertx.config.ApplicationBoot;

/**
 * 自定义需要启动的类
 * @author lifeng
 */
@ApplicationBoot
public class Application {

	public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}