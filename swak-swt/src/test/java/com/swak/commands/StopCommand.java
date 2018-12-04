package com.swak.commands;

import com.swak.manage.command.Command;
import com.swak.manage.command.Commands.Sign;
import com.swak.manage.command.Commands.Signal;

/**
 * 停止按钮
 * 
 * @author lifeng
 */
public class StopCommand implements Command {

	@Override
	public void exec() {
		this.sendSignal(Signal.newSignal(Sign.server_stoping));
		this.log("系统停止中,请稍等...");
		this.stopSystem();
	}

	/**
	 * 停止服务器
	 */
	protected void stopSystem() {
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
			this.sendSignal(Signal.newSignal(Sign.server_stoped));
			this.log("系统已停止");
			this.stoped();
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 停止服务器后的操作
	 */
	protected void stoped() {
	}
}