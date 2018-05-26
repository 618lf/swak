package com.swak.reactivex.web.method;

import com.swak.reactivex.web.annotation.RequestMethod;

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
	Match getMatchingCondition(String lookupPath, RequestMethod lookupMethod);
}
