package com.tmt.manage.command;

/**
 * 执行命令
 * @author lifeng
 */
public interface Command {

	/**
	 * 执行
	 */
	void exec();
	
	/**
	 * 名称
	 * @return
	 */
	default String name() {
		return "";
	}
}