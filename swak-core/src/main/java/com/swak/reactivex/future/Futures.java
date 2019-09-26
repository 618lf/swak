package com.swak.reactivex.future;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.swak.utils.Lists;

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
	public static <T> CompletableFuture<Void> all(List<CompletableFuture<T>> futures) {
		CompletableFuture[] _futures = new CompletableFuture[futures.size()];
		return CompletableFuture.allOf(futures.toArray(_futures));
	}

	/**
	 * 所有结果全部处理完成
	 * 
	 * @param futures
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static <T> CompletableFuture<List<T>> all_result(List<CompletableFuture<T>> futures) {
		CompletableFuture[] _futures = new CompletableFuture[futures.size()];
		return CompletableFuture.allOf(futures.toArray(_futures)).thenApply(res -> {
			List<T> ts = Lists.newArrayList();
			try {
				for (CompletableFuture<T> future : futures) {
					ts.add(future.get());
				}
			} catch (Exception e) {
			}
			return ts;
		});
	}
}