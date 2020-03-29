package com.swak.reactivex.threads;

import java.util.concurrent.Executor;

/**
 * 无线程池 -- 直接之心代码
 *
 * @author: lifeng
 * @date: 2020/3/29 12:27
 */
public enum DirectExecutor implements Executor {

    /**
     * 唯一实例
     */
    INSTANCE;

    @Override
    public void execute(Runnable command) {
        command.run();
    }

    @Override
    public String toString() {
        return "MoreExecutors.directExecutor()";
    }
}