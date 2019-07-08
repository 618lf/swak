package com.swak.reactivex.threads;

import io.netty.channel.EventLoop;

/**
 * 对EventLoop 进行监控,只能做到对這一步
 * 
 * @see 没过细的监控，会有一定的性能影响。
 * @author lifeng
 */
public class EventLoopContext implements Context {

	private final EventLoop eventLoop;

	public EventLoopContext(EventLoop eventLoop) {
		this.eventLoop = eventLoop;
	}

	@Override
	public void execute(Runnable command) {
		eventLoop.execute(() -> executeTask(command));
	}
}