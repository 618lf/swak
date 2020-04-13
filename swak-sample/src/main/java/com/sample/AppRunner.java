package com.sample;

import org.springframework.context.annotation.ComponentScan;

import com.swak.Application;
import com.swak.ApplicationBoot;

/**
 * 系统启动
 * 
 * @author lifeng
 */
@ComponentScan
@ApplicationBoot
public class AppRunner {

	public static void main(String[] args) {
		Application.run(AppRunner.class, args);
	}
}