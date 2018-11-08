package com.tmt.manage.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmt.manage.App;
import com.tmt.manage.command.Command;

/**
 * 开始按钮
 * 
 * @author lifeng
 */
public class StartCommand implements Command{
	private Logger Logger = LoggerFactory.getLogger(App.class);
	@Override
	public void exec() {
		Logger.info("服务器启动中");
	}
}
