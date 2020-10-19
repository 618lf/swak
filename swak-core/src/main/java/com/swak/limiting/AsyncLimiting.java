package com.swak.limiting;

import com.swak.reactivex.threads.Context;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Semaphore;
import java.util.function.Supplier;

/**
 * 异步限流器
 *
 * @author lifeng
 * @date 2020年8月21日 上午11:59:12
 */
public class AsyncLimiting<T> {

    private final Semaphore semaphore;
    private final boolean onceInvoked;
    private Context context;
    private LinkedTransferQueue<AsyncLimitingTask> queue;

    /**
     * 创建顺序执行器
     *
     * @param limit 最大并发
     */
    public AsyncLimiting(int limit) {
        this(limit, false);
    }

    /**
     * 创建顺序执行器
     *
     * @param limit       最大并发, limit <=0 无限制
     * @param onceInvoked 是否只需要执行一次
     */
    public AsyncLimiting(int limit, boolean onceInvoked) {
        this.semaphore = limit <= 0 ? null : new Semaphore(limit);
        this.onceInvoked = onceInvoked;
    }

    /**
     * 顺序的提交代码
     *
     * @param handle 处理器
     * @return 结果
     */
    public CompletableFuture<T> order(Supplier<CompletableFuture<T>> handle) {
        CompletableFuture<T> future = new CompletableFuture<>();
        this.queue.add(new AsyncLimitingTask(future, handle));
        if (this.semaphore == null || this.semaphore.tryAcquire()) {
            this.context.execute(this::run);
        }
        return future;
    }

    /**
     * 任务
     */
    private void run() {

        // 需要执行的任务
        AsyncLimitingTask task = queue.poll();
        if (task == null) {
            if (this.semaphore != null) {
                this.semaphore.release();
            }
            return;
        }

        // 继续下一个任务执行
        task.run().whenComplete(this::completeFutures);
    }

    /**
     * 如果只需要你执行一次则，可以直接完成所有的异步任务
     *
     * @param t 结果
     * @param e 异常
     */
    private void completeFutures(T t, Throwable e) {
        if (this.onceInvoked) {
            while (true) {
                AsyncLimitingTask task = queue.poll();
                if (task == null) {
                    return;
                }
                task.completeFuture(t, e);
            }
        }

        // 保证任务已经全部完成，且释放信号量
        context.execute(this::run);
    }

    /**
     * 是一个异步结果
     *
     * @author lifeng
     * @date 2020年8月21日 上午10:00:46
     */
    public class AsyncLimitingTask {
        Supplier<CompletableFuture<T>> handle;
        CompletableFuture<T> future;

        AsyncLimitingTask(CompletableFuture<T> future, Supplier<CompletableFuture<T>> handle) {
            this.future = future;
            this.handle = handle;
        }

        /**
         * 执行中间代码
         *
         * @return 结果
         */
        public CompletableFuture<T> run() {
            return handle.get().whenComplete(this::completeFuture);
        }

        /**
         * 让外部继续执行下去
         *
         * @param t 结果
         * @param e 异常
         */
        private void completeFuture(T t, Throwable e) {
            if (e != null) {
                future.completeExceptionally(e);
                return;
            }
            future.complete(t);
        }
    }
}
