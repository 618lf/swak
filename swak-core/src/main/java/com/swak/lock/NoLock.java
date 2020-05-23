package com.swak.lock;

import java.util.function.Supplier;

/**
 * 无锁，可以配合OrderInvokeLock高效顺序的执行代码
 *
 * @author: lifeng
 * @date: 2020/3/29 11:55
 */
public class NoLock implements Lock {

    @Override
    public String name() {
        return "NO-LOCK";
    }

    @Override
    public <T> T doHandler(Supplier<T> handler) {
        return handler.get();
    }

    @Override
    public boolean unlock() {
        return true;
    }

    /**
     * 创建一个无锁
     *
     * @return 无锁
     */
    public static NoLock of() {
        return new NoLock();
    }
}