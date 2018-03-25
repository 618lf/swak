package com.swak.http.pool;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;

import com.swak.http.Executeable;

/**
 * 只是简单的执行
 * 
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