package com.tmt.manage.command.impl;

import com.tmt.manage.command.Commands;
import com.tmt.manage.command.Commands.Cmd;
import com.tmt.manage.command.OnceCommand;

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
