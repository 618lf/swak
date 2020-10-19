package com.swak.meters;

/**
 * 定义指标
 *
 * @author: lifeng
 * @date: 2020/3/29 12:05
 */
public interface Metrics {

    /**
     * Used to close out the metrics, for example when an http server/client has
     * been closed.
     * <p>
     * No specific thread and context can be expected when this method is called.
     */
    default void close() {
    }
}
