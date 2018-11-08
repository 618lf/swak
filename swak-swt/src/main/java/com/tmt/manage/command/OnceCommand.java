package com.tmt.manage.command;

/**
 * 只执行一次的命令
 * 
 * @author lifeng
 */
public abstract class OnceCommand implements Command {

	// 记录是否已经执行
	volatile boolean inited = false;

	@Override
	public void exec() {
		if (!inited) {
			try {
				onceExec();
			} catch (Exception e) {
			}
		}
		inited = true;
	}

	/**
	 * 执行一次
	 */
	protected abstract void onceExec();
}
