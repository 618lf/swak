package com.swak.async.tx;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 用于监控事务的结束
 * 
 * @author lifeng
 * @date 2020年10月9日 上午8:43:00
 */
public class TransactionalFuture<T> extends CompletableFuture<TransactionContext> {

	/**
	 * 事务性继续
	 */
	public <U> TransactionalFuture<U> txApply(Function<? super TransactionContext, ? extends U> fn) {
		TransactionalFuture<U> future = new TransactionalFuture<>();
		super.whenComplete((context, e) -> {
			if (e != null || context.getError() != null) {
				future.complete(context.setError(e != null ? e : context.getError()));
			} else if (fn == null) {
				future.complete(context);
			} else {
				this.completeApply(context, fn, future);
			}
		});
		return future;
	}

	/**
	 * 事务性继续
	 */
	public <U> TransactionalFuture<U> txCompose(
			Function<? super TransactionContext, ? extends TransactionalFuture<U>> fn) {
		TransactionalFuture<U> composeFuture = new TransactionalFuture<>();
		super.whenComplete((context, e) -> {
			if (e != null || context.getError() != null) {
				composeFuture.complete(context.setError(e != null ? e : context.getError()));
			} else if (fn == null) {
				composeFuture.complete(context);
			} else {
				this.completeCompose(context, fn, composeFuture);
			}
		});
		return composeFuture;
	}

	/**
	 * 完成事务
	 * 
	 * @param <U>
	 * @param fn
	 * @return
	 */
	public <U> CompletableFuture<U> finish(Function<? super TransactionContext, ? extends U> fn) {
		CompletableFuture<U> future = new CompletableFuture<>();
		super.whenComplete((context, e) -> {
			this.finish(e, context, fn, future);
		});
		return future;
	}

	private <U> void finish(Throwable e, TransactionContext context,
			Function<? super TransactionContext, ? extends U> fn, CompletableFuture<U> future) {
		try {
			context.finish(e).whenComplete((o1, e1) -> {
				if (e1 != null) {
					future.completeExceptionally(e1);
				} else {
					this.completeApply(context, fn, future);
				}
			});
		} catch (Exception ex) {
			future.completeExceptionally(ex);
		}
	}

	private <U> void completeApply(TransactionContext context, Function<? super TransactionContext, ? extends U> fn,
			CompletableFuture<U> future) {
		try {
			U u = fn.apply(context);
			future.complete(u);
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
	}

	private <U> void completeApply(TransactionContext context, Function<? super TransactionContext, ? extends U> fn,
			TransactionalFuture<U> future) {
		try {
			U u = fn.apply(context);
			future.complete(context.setValue(u));
		} catch (Throwable e) {
			future.complete(context.setError(e));
		}
	}

	private <U> void completeCompose(TransactionContext context,
			Function<? super TransactionContext, ? extends TransactionalFuture<U>> fn, TransactionalFuture<U> future) {
		try {
			TransactionalFuture<U> txFuture = fn.apply(context);
			txFuture.whenComplete((r, e) -> {
				if (e != null) {
					future.complete(r.setError(e));
				} else {
					future.complete(r);
				}
			});
		} catch (Throwable e) {
			future.complete(context.setError(e));
		}
	}

	/**
	 * 完成的任务
	 * 
	 * @param context
	 * @return
	 */
	public static <U> TransactionalFuture<U> completedFuture(TransactionContext context) {
		TransactionalFuture<U> future = new TransactionalFuture<>();
		future.complete(context);
		return future;
	}
}