package com.swak.lock;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 异步锁
 *
 * @author lifeng
 * @date 2020年8月21日 下午11:59:44
 */
public interface AsyncLock {

    /**
     * 锁的名称
     *
     * @return 锁的名称
     */
    String name();

    /**
     * 占用锁，如果失败则抛出异常
     *
     * @param lockId 锁ID
     * @return 异步结果
     */
    default CompletableFuture<Void> lock(Long lockId) {
        return null;
    }

    /**
     * 释放锁 -- 特殊情况下，一般不需要执行
     *
     * @param lockId 锁ID
     * @return 是否释放
     */
    default CompletableFuture<Void> unlock(Long lockId) {
        return null;
    }

    /**
     * 执行处理 - 持有锁之后才会执行代码
     *
     * @param lockId  锁ID
     * @param <T>     结果类型
     * @param handler 任务处理
     * @return 结果
     */
    default <T> CompletableFuture<T> doHandler(Long lockId, Supplier<CompletableFuture<T>> handler) {
        CompletableFuture<T> future = new CompletableFuture<>();
        this.lock(lockId).thenCompose((t) -> handler.get()).whenComplete((t, e) -> this.unlock(lockId).whenComplete((t1, e1) -> {
            if (e1 != null) {
                future.completeExceptionally(e1);
                return;
            }
            future.complete(t);
        }));
        return future;
    }

    /**
     * 异步的锁项
     *
     * @return 锁项
     */
    AsyncLockItem newLockItem();

    /**
     * 异步锁
     *
     * @author lifeng
     * @date 2020年8月22日 下午5:43:54
     */
    interface AsyncLockItem {

        /**
         * 执行处理 - 持有锁之后才会执行代码
         *
         * @param handler 任务处理
         * @return 结果
         */
        <T> CompletableFuture<T> doHandler(Supplier<CompletableFuture<T>> handler);
    }
}