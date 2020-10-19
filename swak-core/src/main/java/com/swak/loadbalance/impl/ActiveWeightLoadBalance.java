package com.swak.loadbalance.impl;

import java.util.List;

import com.swak.loadbalance.Referer;

/**
 * 按照使用数
 * 
 * @author lifeng
 * @date 2020年4月30日 上午10:26:43
 */
public class ActiveWeightLoadBalance<T> extends AbstractLoadBalance<T> {

	/**
	 * 最大的查找次数
	 */
	final int MAX_REFERER_COUNT = 10;

	/**
	 * 选择一个小的
	 */
	@Override
	protected Referer<T> doSelect() {

		List<Referer<T>> referers = this.getReferers();

		int refererSize = referers.size();
		int currentCursor = 0;
		Referer<T> selected = null;

		while (currentCursor < refererSize) {
			Referer<T> referer = referers.get(currentCursor % referers.size());
			currentCursor++;

			if (selected == null) {
				selected = referer;
			} else {
				if (selected.getActiveCount() - referer.getActiveCount() > 0) {
					selected = referer;
				}
			}
		}
		return selected;
	}
}