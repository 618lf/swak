package com.swak.commands;

import com.swak.manage.command.Command;
import com.swak.manage.command.Commands;
import com.swak.manage.command.Commands.Cmd;
import com.swak.manage.command.Commands.Sign;
import com.swak.manage.command.Commands.Signal;

/**
 * 开始按钮
 * 
 * @author lifeng
 */
public class StartCommand implements Command {
	
	@Override
	public void exec() {
		this.log("系统启动中...");
		this.sendSignal(Signal.newSignal(Sign.server_starting));
		this.startSystem();
	}
	
	/**
	 * 启动服务
	 */
	private void startSystem() {
		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			this.log("系统启动成功");
			this.sendSignal(Signal.newSignal(Sign.server_started));
			this.log("系统2秒后将打开默认主页");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			Commands.nameCommand(Cmd.url).exec();
		}).start();
	}
}
