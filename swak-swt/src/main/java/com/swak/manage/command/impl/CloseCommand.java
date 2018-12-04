package com.swak.manage.command.impl;

import com.swak.manage.command.Command;
import com.swak.manage.command.Commands.Sign;
import com.swak.manage.command.Commands.Signal;

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
