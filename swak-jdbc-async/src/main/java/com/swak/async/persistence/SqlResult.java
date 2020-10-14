package com.swak.async.persistence;

import java.util.List;

/**
 * 执行结果
 * 
 * @author lifeng
 * @date 2020年10月13日 下午10:48:38
 */
public class SqlResult {

	final Object o;

	public SqlResult(Object o) {
		this.o = o;
	}

	/**
	 * 获取List值
	 * 
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getList() {
		return (T) o;
	}

	/**
	 * 获取int 值
	 * 
	 * @return
	 */
	public Integer getInt() {
		try {
			if (o != null && o instanceof List) {
				List<Integer> ts = this.getList();
				return ts != null && ts.size() > 0 ? (Integer) ts.get(0) : 0;
			}
			return (Integer) o;
		} catch (Exception e) {
			return 0;
		}
	}
}
