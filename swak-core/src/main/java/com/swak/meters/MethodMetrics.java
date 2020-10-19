package com.swak.meters;

/**
 * 方法级别的监控
 *
 * @author: lifeng
 * @date: 2020/3/29 12:04
 */
public interface MethodMetrics<T> extends Metrics {

    /**
     * 开始记录
     *
     * @return 统计对象
     */
    default T begin() {
        return null;
    }

    /**
     * 结束
     *
     * @param t         结束对象
     * @param succeeded 结果
     */
    default void end(T t, boolean succeeded) {
    }
}