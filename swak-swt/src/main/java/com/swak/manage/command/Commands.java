package com.swak.manage.command;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.swak.manage.command.impl.BrowserOpenedCommand;
import com.swak.manage.command.impl.CloseCommand;
import com.swak.manage.command.impl.InitCommand;

/**
 * 管理命令
 * 
 * @author lifeng
 */
public class Commands {

	// 存储所有的命令
	private static Map<Cmd, Command> commands = new HashMap<>();

	// 存储的信号
	private static BlockingQueue<Signal> signals = new LinkedBlockingQueue<Signal>();

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
		commands.put(Cmd.open, new BrowserOpenedCommand());
		commands.put(Cmd.close, new CloseCommand());
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
	 * 发送信号
	 * 
	 * @param signal
	 */
	public static void sendSignal(Signal signal) {
		try {
			signals.put(signal);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * 获得信号
	 * 
	 * @param signal
	 */
	public static Signal receiveSignal() {
		try {
			return signals.take();
		} catch (InterruptedException e) {
			return null;
		}
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
		public void exec(Object param) {
			if (commands.get(name) != null) {
				commands.get(name).exec(param);
			}
		}

		@Override
		public String name() {
			return name.getName();
		}
	}

	/**
	 * 信号
	 * 
	 * @author lifeng
	 */
	public static class Signal {

		private Sign sign;
		private String remarks;

		public Sign getSign() {
			return sign;
		}

		public void setSign(Sign sign) {
			this.sign = sign;
		}

		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		public static Signal newSignal(Sign sign) {
			Signal signal = new Signal();
			signal.setSign(sign);
			return signal;
		}

		public static Signal newSignal(Sign sign, String remarks) {
			Signal signal = new Signal();
			signal.setSign(sign);
			signal.setRemarks(remarks);
			return signal;
		}
	}

	/**
	 * 支持的命令
	 * 
	 * @author lifeng
	 */
	public static enum Cmd {
		init("初始化"), task("任务"), start("启动"), open("主页"), url("打开地址"), stop("停止"), close("关闭"), exit("退出"), Dispose(
				"释放"), Deactivated("非激活"), Deiconified("非最小化"), Iconified("最小化"), starter("正常模式"), upgrader("升级模式");

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

	/**
	 * 支持的信号
	 * 
	 * @author lifeng
	 */
	public static enum Sign {
		/** Netty */
		server_starting, server_started, server_stoping, server_stoped,
		/** 日志 */
		log, upgrade, upgraded,
		/** 浏览器 */
		browser_opened, browser_closed, browser,
		/** SWT */
		window_close, window_exit
	}
}