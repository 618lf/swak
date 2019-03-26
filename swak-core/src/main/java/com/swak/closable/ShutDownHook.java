package com.swak.closable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 参考 motan做的一个全局资源释放工具
 * 
 * @author lifeng
 */
public final class ShutDownHook extends Thread {

	private static CompletableFuture<Void> shutDownFuture = new CompletableFuture<>();
	private static final int DEFAULT_PRIORITY = 20;

	private static class ShutDownHookHolder {
		private final static ShutDownHook instance = new ShutDownHook();
	}

	private ArrayList<ClosableObject> resourceList = new ArrayList<ClosableObject>();

	private ShutDownHook() {
	}

	@Override
	public void run() {
		closeAll();
	}

	// synchronized method to close all the resources in the list
	private synchronized void closeAll() {
		Collections.sort(resourceList);
		for (ClosableObject resource : resourceList) {
			try {
				resource.closable.close();
			} catch (Exception e) {
			}
		}
		resourceList.clear();
		shutDownFuture.complete(null);
	}

	/**
	 * 等待关闭
	 * 
	 * @param sync
	 */
	public static void onClose() {
		Thread thread = new Thread(() -> {
			try {
				ShutDownHook.shutDownFuture.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		});
		thread.setDaemon(false);
		thread.setName("App.Server-closeAwait");
		thread.start();
	}

	/**
	 * 执行关闭
	 * 
	 * @param sync
	 */
	public static void runHook(boolean sync) {
		if (sync) {
			ShutDownHookHolder.instance.run();
		} else {
			ShutDownHookHolder.instance.start();
		}
	}

	/**
	 * 添加关闭
	 * 
	 * @param closable
	 * @param priority
	 */
	public static void registerShutdownHook(Closable closable) {
		registerShutdownHook(closable, DEFAULT_PRIORITY);
	}

	/**
	 * 添加关闭
	 * 
	 * @param closable
	 * @param priority
	 */
	public static synchronized void registerShutdownHook(Closable closable, int priority) {
		ShutDownHookHolder.instance.resourceList.add(new ClosableObject(closable, priority));
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
			if (this.priority > o.priority) {
				return -1;
			} else if (this.priority == o.priority) {
				return 0;
			} else {
				return 1;
			}
		}
	}
}
