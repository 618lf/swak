package com.swak.manage.command.impl;

import com.swak.manage.command.Commands;
import com.swak.manage.command.OnceCommand;
import com.swak.manage.command.Commands.Cmd;

/**
 * 初始化
 * 
 * @author lifeng
 */
public class InitCommand extends OnceCommand{
	
	@Override
	protected void onceExec() {
		this.log("系统初始化...");
		Commands.nameCommand(Cmd.start).exec();
	}
}
