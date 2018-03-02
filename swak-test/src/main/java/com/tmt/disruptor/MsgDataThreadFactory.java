package com.tmt.disruptor;

import java.util.concurrent.ThreadFactory;

public enum MsgDataThreadFactory implements ThreadFactory {

	INSTANCE;

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		return thread;
	}
}