package com.sample.tools;

import com.sample.tools.config.Settings;
import com.swak.Application;
import com.swak.ApplicationBoot;

/**
 * 系统启动
 * 
 * @author lifeng
 */
@ApplicationBoot
public class AppRunner {

	public static void main(String[] args) {
		Settings.intSettings();
		Application.run(AppRunner.class, args);
	}
}