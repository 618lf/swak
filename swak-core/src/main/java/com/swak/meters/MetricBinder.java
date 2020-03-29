package com.swak.meters;

import com.swak.meters.MetricsFactory;

/**
 * 绑定到指标收集器
 *
 * @author: lifeng
 * @date: 2020/3/29 12:04
 */
@FunctionalInterface
public interface MetricBinder {

    /**
     * 指定收集器
     *
     * @param metricsFactory 收集器
     */
    void bindTo(MetricsFactory metricsFactory);
}