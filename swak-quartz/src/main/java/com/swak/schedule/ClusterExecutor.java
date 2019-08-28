package com.swak.schedule;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.operations.AsyncOperations;
import com.swak.rabbit.EventBus;
import com.swak.utils.IOUtils;

import io.lettuce.core.ScriptOutputType;

/**
 * 集群版本的处理器
 * 
 * @author lifeng
 */
public abstract class ClusterExecutor extends StandardExecutor {

	private static String dispatch = null;
	private static String dispatch_Sha = null;

	static {
		try {
			List<String> lines = IOUtils.readLines(ClusterExecutor.class.getResourceAsStream("dispatch.lua"));
			StringBuilder _sc = new StringBuilder();
			lines.stream().forEach(s -> _sc.append(s).append("\n"));
			dispatch = _sc.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 任务名称
	 * 
	 * @return
	 */
	protected abstract String name();

	/**
	 * 仅仅分配调度任务
	 */
	@Override
	protected Object doTask() {
		return this.doDispatch();
	}

	/**
	 * 将 status 从 -1 改为 0 返回 true。如果允许并行执行，则为0也返回true，否则返回false。
	 * 
	 * @return
	 */
	protected Object doDispatch() {
		return this.tryDispatch().thenApply(res -> {
			String result = res != null ? SafeEncoder.encode((byte[]) res) : "0";
			return !"0".equals(result);
		}).thenCompose(res -> {
			if (res) {
				return EventBus.me().submit(name(), name(), name());
			}
			return CompletableFuture.completedFuture(null);
		});
	}

	protected CompletionStage<byte[]> tryDispatch() {
		byte[][] values = new byte[][] { SafeEncoder.encode(name()) };
		if (dispatch_Sha == null) {
			return AsyncOperations.loadScript(dispatch).thenCompose(res -> {
				dispatch_Sha = res;
				return AsyncOperations.runShaScript(dispatch_Sha, ScriptOutputType.VALUE, values);
			});
		} else {
			return AsyncOperations.runShaScript(dispatch_Sha, ScriptOutputType.VALUE, values);
		}
	}
}