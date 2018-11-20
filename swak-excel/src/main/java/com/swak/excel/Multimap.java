package com.swak.excel;

import java.util.List;
import java.util.Map;

import com.swak.utils.Lists;
import com.swak.utils.Maps;

public class Multimap<K, T> {

	private Map<K, List<T>> values = null;

	public Multimap() {
		values = Maps.newHashMap();
	}

	/**
	 * 获取列表数据
	 * 
	 * @param k
	 * @return
	 */
	public Iterable<T> get(K k) {
		return values.get(k);
	}

	/**
	 * 存储列表数据
	 * 
	 * @param k
	 * @param t
	 */
	public void put(K k, T t) {
		List<T> ts = values.get(k);
		if (ts == null) {
			ts = Lists.newArrayList();
			values.put(k, ts);
		}
		ts.add(t);
	}
}
