package com.swak.http.pool;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;

import com.swak.http.Executeable;

/**
 * @Suspendable 需要编译的支持，配置很麻烦
 * @author lifeng
 */
public class AsyncExecutor implements Executeable {

	private int stackSize = -1;

	@Override
	@Suspendable
	public void onExecute(String lookupPath, Runnable run){
		new Fiber<Void>(null, stackSize, () -> {
			exec(lookupPath, run);
		}).start();
	}
	
	@Suspendable
	final void exec(String lookupPath, Runnable run) throws InterruptedException, SuspendExecution{
		run.run();
	}
}