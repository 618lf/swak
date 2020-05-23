package com.swak.loadbalance.impl;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.swak.loadbalance.Referer;

/**
 * 随机
 * 
 * @author lifeng
 * @date 2020年4月30日 上午10:25:27
 */
public class RandomLoadBalance<T> extends AbstractLoadBalance<T> {

	@Override
	protected Referer<T> doSelect() {
		List<Referer<T>> referers = this.getReferers();
		int index = (int) (ThreadLocalRandom.current().nextDouble() * referers.size());
		return referers.get(index % referers.size());
	}
}
