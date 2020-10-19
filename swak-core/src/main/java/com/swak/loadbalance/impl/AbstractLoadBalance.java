package com.swak.loadbalance.impl;

import com.swak.exception.DataAccessException;
import com.swak.loadbalance.LoadBalance;
import com.swak.loadbalance.Referer;
import com.swak.utils.Lists;

import java.util.List;

public abstract class AbstractLoadBalance<T> implements LoadBalance<T> {

    List<Referer<T>> referers;

    public List<Referer<T>> getReferers() {
        return referers;
    }

    @Override
    public void onRefresh(List<T> referers) {
        List<Referer<T>> refs = Lists.newArrayList(referers.size());
        for (int i = 0; i < referers.size(); i++) {
            T t = referers.get(i);
            Referer<T> ref = new Referer<>();
            ref.ref = t;
            ref.name = prefix() + (i + 1);
            refs.add(ref);
        }
        this.referers = refs;
    }

    /**
     * 默认的前缀
     */
    protected String prefix() {
        return "ref_";
    }

    @Override
    public T select() {
        List<Referer<T>> referers = this.getReferers();
        if (referers == null) {
            throw new DataAccessException(this.getClass().getSimpleName() + " No available referers:");
        }
        Referer<T> ref = null;
        if (referers.size() > 1) {
            ref = doSelect();
        } else if (referers.size() == 1) {
            ref = referers.get(0);
        }

        if (ref != null) {
            return ref.select();
        }
        throw new DataAccessException(this.getClass().getSimpleName() + " No available referers");
    }

    /**
     * 选择一个依赖
     *
     * @return 依赖
     */
    protected abstract Referer<T> doSelect();
}