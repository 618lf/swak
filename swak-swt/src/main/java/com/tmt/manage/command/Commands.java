package com.tmt.manage.command;

import java.util.HashMap;
import java.util.Map;

import com.tmt.manage.command.impl.InitCommand;
import com.tmt.manage.command.impl.StartCommand;
import com.tmt.manage.command.impl.StopCommand;
import com.tmt.manage.command.impl.TouchCommand;

/**
 * 管理命令
 * 
 * @author lifeng
 */
public class Commands {

	// 存储所有的命令
	private static Map<Cmd, Command> commands = new HashMap<>();

	/**
	 * 注册命令
	 * 
	 * @param name
	 * @param command
	 */
	public static void register(Cmd cmd, Command command) {
		commands.put(cmd, command);
	}
	
	/**
	 * 注册默认命令
	 */
	public static void registers() {
		commands.put(Cmd.init, new InitCommand());
		commands.put(Cmd.start, new StartCommand());
		commands.put(Cmd.stop, new StopCommand());
		commands.put(Cmd.task, new TouchCommand());
	}
	
	/**
	 * 约定使用指定的命令
	 * 
	 * @param name
	 * @return
	 */
	public static Command nameCommand(Cmd name) {
		return new NamedCommand(name);
	}

	/**
	 * 通过名称来执行命令
	 * 
	 * @author lifeng
	 */
	public static class NamedCommand implements Command {

		private Cmd name;

		public NamedCommand(Cmd name) {
			this.name = name;
		}

		@Override
		public void exec() {
			if (commands.get(name) != null) {
				commands.get(name).exec();
			}
		}

		@Override
		public String name() {
			return name.getName();
		}
	}
	
	/**
	 * 支持的命令
	 * 
	 * @author lifeng
	 */
	public static enum Cmd {
		init("初始化"), task("任务"), start("开始"), stop("结束"), Deactivated("非激活"), Deiconified("非最小化"), Iconified("最小化");
		
		private String name;
		
		private Cmd(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}