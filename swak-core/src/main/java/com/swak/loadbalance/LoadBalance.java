package com.swak.loadbalance;

import java.util.List;

/**
 * Loadbalance for select DataSource
 *
 * @author lifeng
 * @date 2020年4月29日 下午5:44:00
 */
public interface LoadBalance<T> {

    /**
     * 刷新数据源
     *
     * @param referers 依赖
     */
    void onRefresh(List<T> referers);

    /**
     * 选择一个正式的数据源
     *
     * @return 选择一个
     */
    T select();
}