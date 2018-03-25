package com.swak.http.pool;

import com.swak.http.Executeable;

import jsr166e.CompletableFuture;

/**
 * 只是简单的执行
 * @author lifeng
 */
public class AsyncExecutor implements Executeable {

	
	@Override
	public void onExecute(String lookupPath, Runnable run) {
		CompletableFuture.runAsync(run);
	}
}