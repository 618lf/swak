package com.swak.meters;

/**
 * 方法级别的监控
 *
 * @author: lifeng
 * @date: 2020/3/29 12:04
 */
public interface MethodMetrics<T> extends Metrics {

    default T begin() {
        return null;
    }

    default void end(T t, boolean succeeded) {
    }
}