package com.swak.async.tx;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * 用于监控事务的结束
 * 
 * @author lifeng
 * @date 2020年10月9日 上午8:43:00
 */
public class TransactionalFuture extends CompletableFuture<TransactionContext> {

	/**
	 * 事务性继续
	 */
	public <U> TransactionalFuture txApply(Function<? super TransactionContext, ? extends U> fn) {
		TransactionalFuture future = new TransactionalFuture();
		super.whenComplete((context, e) -> {
			if (e != null) {
				future.completeExceptionally(e);
			} else if (context == null) {
				future.completeExceptionally(new TransactionLoseException());
			} else {
				this.completeApply(context, fn, future);
			}
		});
		return future;
	}

	/**
	 * 事务性继续
	 */
	public <U> TransactionalFuture txCompose(Function<? super TransactionContext, ? extends TransactionalFuture> fn) {
		TransactionalFuture composeFuture = new TransactionalFuture();
		super.whenComplete((context, e) -> {
			if (e != null) {
				composeFuture.completeExceptionally(e);
			} else if (context == null) {
				composeFuture.completeExceptionally(new TransactionLoseException());
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
			TransactionalFuture future) {
		try {
			U u = fn.apply(context);
			context.setValue(u);
			future.complete(context);
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
	}

	private void completeCompose(TransactionContext context,
			Function<? super TransactionContext, ? extends TransactionalFuture> fn, TransactionalFuture future) {
		try {
			TransactionalFuture txFuture = fn.apply(context);
			txFuture.whenComplete((r, e) -> {
				if (e != null) {
					future.completeExceptionally(e);
				} else {
					future.complete(r);
				}
			});
		} catch (Exception e) {
			future.completeExceptionally(e);
		}
	}

	/**
	 * 完成的任务
	 * 
	 * @param context
	 * @return
	 */
	public static TransactionalFuture completedFuture(TransactionContext context) {
		TransactionalFuture future = new TransactionalFuture();
		future.complete(context);
		return future;
	}
}