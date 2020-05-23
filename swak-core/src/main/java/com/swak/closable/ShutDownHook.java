package com.swak.closable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * 参考 motan做的一个全局资源释放工具
 *
 * @author: lifeng
 * @date: 2020/3/29 10:14
 */
public final class ShutDownHook extends Thread {

	private static CompletableFuture<Void> shutDownFuture = new CompletableFuture<>();
	private static final int DEFAULT_PRIORITY = 20;

	private static class ShutDownHookHolder {
		private final static ShutDownHook INSTANCE = new ShutDownHook();
	}

	private ArrayList<ClosableObject> resourceList = new ArrayList<>();

	private ShutDownHook() {
	}

	@Override
	public void run() {
		closeAll();
	}

	/**
	 * synchronized method to close all the resources in the list
	 *
	 * @author lifeng
	 * @date 2020/3/29 10:15
	 */
	private synchronized void closeAll() {
		Collections.sort(resourceList);
		for (ClosableObject resource : resourceList) {
			try {
				resource.closable.close();
			} catch (Exception ignored) {
			}
		}
		resourceList.clear();
		shutDownFuture.complete(null);
	}

	/**
	 * 执行关闭
	 *
	 * @param sync 是否同步，如果同步则会在当前线程中执行
	 */
	public static void runHook(boolean sync) {
		if (sync) {
			ShutDownHookHolder.INSTANCE.closeAll();
		} else {
			ShutDownHookHolder.INSTANCE.start();
		}
	}

	/**
	 * 添加关闭任务
	 *
	 * @param closable 添加关闭任务
	 */
	public static void registerShutdownHook(Closable closable) {
		registerShutdownHook(closable, DEFAULT_PRIORITY);
	}

	/**
	 * 添加关闭
	 *
	 * @param closable 添加关闭任务
	 * @param priority 优先级
	 */
	public static synchronized void registerShutdownHook(Closable closable, int priority) {
		ShutDownHookHolder.INSTANCE.resourceList.add(new ClosableObject(closable, priority));
	}

	/**
	 * 可以排序的关闭实体
	 *
	 * @author lifeng
	 */
	private static class ClosableObject implements Comparable<ClosableObject> {
		Closable closable;
		int priority;

		public ClosableObject(Closable closable, int priority) {
			this.closable = closable;
			this.priority = priority;
		}

		@Override
		public int compareTo(ClosableObject o) {
			return Integer.compare(o.priority, this.priority);
		}
	}
}
