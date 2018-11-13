package com.tmt;

import com.tmt.commands.ExitCommand;
import com.tmt.commands.StartCommand;
import com.tmt.commands.StopCommand;
import com.tmt.commands.TouchCommand;
import com.tmt.commands.UrlCommand;
import com.tmt.manage.App;
import com.tmt.manage.command.Commands;
import com.tmt.manage.command.Commands.Cmd;

/**
 * 测试启动
 * 
 * @author lifeng
 */
public class Starter extends App {

	/**
	 * 注册命令
	 */
	@Override
	protected void commands() {
		Commands.registers();
		Commands.register(Cmd.start, new StartCommand());
		Commands.register(Cmd.stop, new StopCommand());
		Commands.register(Cmd.exit, new ExitCommand());
		Commands.register(Cmd.task, new TouchCommand());
		Commands.register(Cmd.url, new UrlCommand());
	}
	
	/**
	 * 后期可以自定义主题
	 */
	@Override
	protected Theme theme() {
		return Theme.Orange;
	}

	/**
	 * 启动服务
	 * @param args
	 */
	public static void main(String[] args) {
		new Starter().run(new String[] {"com/tmt/Starter.class"});
	}
}
