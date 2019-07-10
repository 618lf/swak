package com.swak.lock;

import java.util.function.Supplier;

/**
 * @see 基本目的：锁的目的是保证数据的安全性，不让多线程来执行一段代码
 * @see 如果能在不阻塞线程的情况下，实现基本目的就更好了.
 * 
 * @author lifeng
 */
public interface Lock {

	/**
	 * 锁的名称
	 * 
	 * @return
	 */
	String name();
	
	/**
	 * 执行处理 - 持有锁之后才会执行代码
	 * 
	 * @param handler
	 * @return
	 */
	<T> T doHandler(Supplier<T> handler);
	
	/**
	 * 释放锁 -- 特殊情况下，一般不需要执行
	 * 
	 * @return
	 */
	boolean unlock();
}
