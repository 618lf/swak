package com.swak.boot;

/**
 * 异步启动
 * @author root
 */
public abstract class AbstractBoot implements Boot{

	/**
	 * 实现异步启动
	 */
	@Override
	public void start() {
		Thread task =  new Thread(new Runnable() {
			@Override
			public void run() {
				AbstractBoot.this.init();
			}
		});
		task.setName("Boot-Task for:" + this.describe());
		task.setDaemon(true);
		task.start();
	}
	
	/**
	 * 实现初始化工作
	 */
	public abstract void init();
}