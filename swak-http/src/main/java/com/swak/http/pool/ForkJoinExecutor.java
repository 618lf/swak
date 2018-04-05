package com.swak.http.pool;

import com.swak.http.Executeable;

import jsr166e.CompletableFuture;

/**
 * 简单的异步执行即可
 * @author lifeng
 */
public class ForkJoinExecutor implements Executeable{

	@Override
	public void onExecute(String lookupPath, Runnable run) {
		CompletableFuture.runAsync(run);
	}
}