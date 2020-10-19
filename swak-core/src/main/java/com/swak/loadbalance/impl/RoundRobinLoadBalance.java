package com.swak.loadbalance.impl;

import com.swak.loadbalance.Referer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询
 *
 * @author lifeng
 * @date 2020年4月30日 上午10:25:39
 */
public class RoundRobinLoadBalance<T> extends AbstractLoadBalance<T> {

    private AtomicInteger idx = new AtomicInteger(0);

    @Override
    protected Referer<T> doSelect() {
        List<Referer<T>> referers = this.getReferers();
        int index = getNonNegative();
        return referers.get(index % referers.size());
    }

    /**
     * 通过二进制位操作将originValue转化为非负数: 0和正数返回本身
     * 负数通过二进制首位取反转化为正数或0（Integer.MIN_VALUE将转换为0） return non-negative int value of
     * originValue
     *
     * @return positive int
     */
    public int getNonNegative() {
        return 0x7fffffff & idx.incrementAndGet();
    }
}
