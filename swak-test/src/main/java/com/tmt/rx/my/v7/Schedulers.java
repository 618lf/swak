package com.tmt.rx.my.v7;

import java.util.concurrent.Executors;

/**
 * 任务调度器工厂
 * @author lifeng
 */
public class Schedulers {
	private static final Scheduler ioScheduler = new Scheduler(Executors.newSingleThreadExecutor());
    public static Scheduler io() {
        return ioScheduler;
    }
}
