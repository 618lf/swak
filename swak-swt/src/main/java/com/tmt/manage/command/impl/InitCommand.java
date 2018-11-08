package com.tmt.manage.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmt.manage.App;
import com.tmt.manage.command.OnceCommand;

/**
 * 初始化
 * 
 * @author lifeng
 */
public class InitCommand extends OnceCommand{

	private Logger Logger = LoggerFactory.getLogger(App.class);
	
	@Override
	protected void onceExec() {
		Logger.info("系统初始化");
	}
}
