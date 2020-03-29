package com.swak.annotation;

/**
 * 四种上下文模式
 *
 * @author: lifeng
 * @date: 2020/3/28 17:11
 */
public enum Context {

    /**
     * 适合异步 IO 的运行模式
     */
    IO,

    /**
     * 顺序的执行
     */
    Order,

    /**
     * 并发的运行模式
     */
    Concurrent
}
