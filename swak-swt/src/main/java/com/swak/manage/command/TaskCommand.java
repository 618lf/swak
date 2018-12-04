package com.swak.manage.command;

/**
 * 任务命令
 * 
 * @author lifeng
 */
public abstract class TaskCommand implements Command {
	
	private Thread thread;

	public TaskCommand() {
		thread = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(1000);
					this.exec();
				} catch (InterruptedException e) {}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
}