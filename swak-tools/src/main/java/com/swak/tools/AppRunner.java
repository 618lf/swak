package com.swak.tools;

import com.swak.Application;
import com.swak.ApplicationBoot;
import com.swak.tools.config.Settings;

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