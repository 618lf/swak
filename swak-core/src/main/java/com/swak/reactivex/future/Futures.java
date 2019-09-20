package com.swak.reactivex.future;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 快捷处理异步结果
 * 
 * @author lifeng
 */
public class Futures {

	/**
	 * 所有结果全部处理完成
	 * 
	 * @param futures
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static CompletableFuture<Void> all(List<CompletableFuture<Void>> futures) {
		CompletableFuture[] _futures = new CompletableFuture[futures.size()];
		return CompletableFuture.allOf(futures.toArray(_futures));
	}
}