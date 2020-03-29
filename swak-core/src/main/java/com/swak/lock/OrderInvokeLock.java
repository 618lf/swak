package com.swak.lock;

import com.swak.reactivex.threads.Contexts;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 按顺序执行代码的锁，目的不阻塞线程 请注册到spring中
 *
 * @author: lifeng
 * @date: 2020/3/29 11:58
 */
public class OrderInvokeLock implements AsyncLock, DisposableBean {

    protected final ExecutorService executor;
    protected final Lock lock;

    /**
     * 创建顺序锁
     *
     * @param lock           -- 真实的锁，局部锁，全局锁
     * @param maxExecSeconds -- 最大的执行时间
     */
    public OrderInvokeLock(Lock lock, int maxExecSeconds) {
        this.executor = Contexts.createWorkerContext("SWAK.lock-" + lock.name() + "-", 1, true, maxExecSeconds,
                TimeUnit.SECONDS);
        this.lock = lock;
    }

    /**
     * @param lock                     -- 真实的锁，局部锁，全局锁
     * @param maxExecSeconds           -- 最大的执行时间
     * @param maxQueue                 -- 最大队列
     * @param rejectedExecutionHandler -- 队列满处理方式
     */
    public OrderInvokeLock(Lock lock, int maxExecSeconds, int maxQueue, RejectedExecutionHandler rejectedExecutionHandler) {
        this.executor = Contexts.createWorkerContext("SWAK.lock-" + lock.name() + "-", 1, true, maxExecSeconds,
                TimeUnit.SECONDS, maxQueue, rejectedExecutionHandler);
        this.lock = lock;
    }

    /**
     * 执行代码 后续代码必须切换线程执行
     *
     * @param handler 处理
     * @return 异步结果
     */
    @Override
    public <T> CompletableFuture<T> execute(Supplier<T> handler) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executor.execute(() -> {
            T value;
            try {
                value = this.doHandler(handler);
            } catch (Exception e) {
                future.completeExceptionally(e);
                return;
            }
            future.complete(value);
        });
        return future;
    }

    /**
     * 销毁
     */
    @Override
    public void destroy() {
        try {
            executor.shutdown();
            int awaitTime = 30;
            if (!executor.awaitTermination(awaitTime, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        try {
            this.unlock();
        } catch (Exception ignored) {
        }
    }

    @Override
    public String name() {
        return lock.name();
    }

    @Override
    public <T> T doHandler(Supplier<T> handler) {
        return lock.doHandler(handler);
    }

    @Override
    public boolean unlock() {
        return lock.unlock();
    }

    /**
     * 异步锁
     *
     * @param lock           锁
     * @param maxExecSeconds 最大执行时间
     */
    public static AsyncLock of(Lock lock, int maxExecSeconds) {
        return new OrderInvokeLock(lock, maxExecSeconds);
    }
}