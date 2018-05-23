package com.swak.common.executor;

import java.util.concurrent.Executor;

/**
 * 执行器包装
 * @author lifeng
 */
public class Workers {
	private static Executor executor;
	public static void executor(Executor executor) {
		Workers.executor =executor;
	}
	public static Executor executor() {
		return executor;
	}
}