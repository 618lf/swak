package com.swak.lock;

import java.util.function.Supplier;

import com.swak.exception.LockTimeOutException;

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
	 * 尝试占用锁， 不会阻塞线程，也不会抛出异常
	 * 
	 * @return
	 */
	boolean tryLock();

	/**
	 * 占用锁，如果失败则抛出异常
	 * 
	 * @return
	 */
	boolean lock();

	/**
	 * 释放锁 -- 特殊情况下，一般不需要执行
	 *
	 * @return 是否释放
	 */
	boolean unlock();

	/**
	 * 执行处理 - 持有锁之后才会执行代码
	 *
	 * @param handler 任务处理
	 * @return 结果
	 */
	default <T> T doHandler(Supplier<T> handler) {
		boolean hasLock = false;
		try {
			if (!!(hasLock = this.lock())) {
				return handler.get();
			}
			throw new LockTimeOutException("锁 " + name() + " 超时");
		} finally {
			if (hasLock) {
				this.unlock();
			}
		}
	}
}
