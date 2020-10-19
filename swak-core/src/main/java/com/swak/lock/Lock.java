package com.swak.lock;

import java.util.function.Supplier;

/**
 * 基本目的：锁的目的是保证数据的安全性，不让多线程来执行一段代码 如果能在不阻塞线程的情况下，实现基本目的就更好了.
 *
 * @author lifeng
 */
public interface Lock {

	/**
	 * 锁的名称
	 *
	 * @return 锁的名称
	 */
	String name();

	/**
	 * 占用锁，如果失败则抛出异常
	 */
	default void lock() {
	}

	/**
	 * 释放锁 -- 特殊情况下，一般不需要执行
	 *
	 * @return 是否释放
	 */
	default void unlock() {
	}

	/**
	 * 执行处理 - 持有锁之后才会执行代码
	 *
	 * @param handler 任务处理
	 * @return 结果
	 */
	default <T> T doHandler(Supplier<T> handler) {
		try {
			this.lock();
			return handler.get();
		} finally {
			this.unlock();
		}
	}
}
