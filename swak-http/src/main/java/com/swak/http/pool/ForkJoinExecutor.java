package com.swak.http.pool;

import java.util.concurrent.CompletableFuture;

import com.swak.http.Executeable;

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