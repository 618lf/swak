package com.tmt.manage.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tmt.manage.App;
import com.tmt.manage.command.Commands.Signal;

/**
 * 执行命令
 * @author lifeng
 */
public interface Command {
	
	Logger LOGGER = LoggerFactory.getLogger(App.class);
	
	/**
	 * 执行
	 */
	void exec();
	
	/**
	 * 待参数执行
	 * 
	 * @param param
	 */
	default void exec(Object param) {
	}
	
	/**
	 * 名称
	 * @return
	 */
	default String name() {
		return "";
	}
	
	/**
	 * 发送信号
	 * @param signal
	 * @param desc
	 */
	default void log(String text) {
		LOGGER.info(text);
	}
	
	/**
	 * 发送信号
	 * @param signal
	 * @param desc
	 */
	default void sendSignal(Signal signal) {
		Commands.sendSignal(signal);
	}
}