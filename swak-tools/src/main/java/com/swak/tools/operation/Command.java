package com.swak.tools.operation;

/**
 * 命令
 * 
 * @author lifeng
 */
public interface Command {

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
}
