package com.swak.mvc.method;

import java.util.Set;

public interface RequestCondition<T> {

	/**
	 * 合并
	 * @param other
	 * @return
	 */
	T combine(T other);
	
	/**
	 * 获取匹配条件
	 * @param request
	 * @return
	 */
	Set<String> getMatchingCondition(String lookupPath);
}
