package com.tmt.manage.command.impl;

import com.tmt.manage.command.Command;
import com.tmt.manage.command.Commands.Sign;
import com.tmt.manage.command.Commands.Signal;

/**
 * 关闭
 * 
 * @author lifeng
 */
public class CloseCommand implements Command{

	@Override
	public void exec() {
		this.sendSignal(Signal.newSignal(Sign.window_close));
	}
}
