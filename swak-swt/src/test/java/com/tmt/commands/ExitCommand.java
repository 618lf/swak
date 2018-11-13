package com.tmt.commands;

import com.tmt.manage.command.Commands.Sign;
import com.tmt.manage.command.Commands.Signal;

/**
 * 退出命令
 * 
 * @author lifeng
 */
public class ExitCommand extends StopCommand {

	@Override
	protected void stoped() {
		this.sendSignal(Signal.newSignal(Sign.exit));
	}
}
