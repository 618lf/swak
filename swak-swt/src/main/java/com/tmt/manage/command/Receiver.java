package com.tmt.manage.command;

import com.tmt.manage.command.Commands.Signal;

/**
 * 信号接收器
 * 
 * @author lifeng
 */
public interface Receiver {

	/**
	 * 处理信号
	 * 
	 * @param signal
	 */
	void handleSignal(Signal signal);
}