package com.tmt.manage.command.impl;

import com.tmt.manage.command.Command;
import com.tmt.manage.command.Commands.Sign;
import com.tmt.manage.command.Commands.Signal;
import com.tmt.manage.config.Settings;

/**
 * 开始按钮
 * 
 * @author lifeng
 */
public class StartCommand implements Command {
	
	@Override
	public void exec() {
		this.log("系统启动中...");
		this.sendSignal(Signal.newSignal(Sign.starting));
		this.startSystem();
	}
	
	/**
	 * 启动服务
	 */
	private void startSystem() {
		new Thread(() -> {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {}
			this.log("系统启动成功");
			this.sendSignal(Signal.newSignal(Sign.started));
			this.log("系统主页：" + Settings.getSettings().getServerPage());
		}).start();
	}
}
