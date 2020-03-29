package com.swak.boot;

/**
 * 异步启动
 *
 * @author: lifeng
 * @date: 2020/3/29 9:56
 */
public abstract class AbstractBoot implements Boot {

    /**
     * 实现异步启动
     */
    @Override
    public void start() {
        Thread task = new Thread(AbstractBoot.this::init);
        task.setName("Boot-Task for:" + this.describe());
        task.setDaemon(true);
        task.start();
    }

    /**
     * 实现初始化工作
     */
    public abstract void init();
}