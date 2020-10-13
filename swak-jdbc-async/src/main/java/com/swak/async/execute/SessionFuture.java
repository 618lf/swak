package com.swak.async.execute;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * Session 执行结果
 * 
 * @author lifeng
 * @date 2020年10月13日 下午10:08:26
 */
class SessionFuture<T> extends CompletableFuture<T> {

	final SqlSession session;

	public SessionFuture(SqlSession session) {
		this.session = session;
	}

	/**
	 * 完成事务
	 * 
	 * @param <U>
	 * @param fn
	 * @return
	 */
	public void finish(BiConsumer<? super T, ? super Throwable> fn) {
		super.whenComplete((t, e) -> {
			this.finish(e, t, fn);
		});
	}

	private <U> void finish(Throwable e, T t, BiConsumer<? super T, ? super Throwable> fn) {
		session.finish(e).whenComplete((o1, e1) -> {
			if (e1 != null) {
				this.completeApply(e1, t, fn);
			} else {
				this.completeApply(e, t, fn);
			}
		});
	}

	private <U> void completeApply(Throwable e, T t, BiConsumer<? super T, ? super Throwable> fn) {
		try {
			fn.accept(t, e);
		} catch (Exception ex) {
		}
	}
}
