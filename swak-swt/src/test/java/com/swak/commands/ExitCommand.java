package com.swak.commands;

import com.swak.manage.command.Commands.Sign;
import com.swak.manage.command.Commands.Signal;

/**
 * 退出命令
 * 
 * @author lifeng
 */
public class ExitCommand extends StopCommand {

	@Override
	protected void stoped() {
		this.sendSignal(Signal.newSignal(Sign.window_exit));
	}
}
