package com.swak.async.persistence;

import java.util.List;

/**
 * 执行结果
 * 
 * @author lifeng
 * @date 2020年10月13日 下午10:48:38
 */
@SuppressWarnings("rawtypes")
public class SqlResult {

	final List o;

	public SqlResult(List o) {
		this.o = o;
	}

	@SuppressWarnings("unchecked")
	public <T> T get() {
		return (T) o;
	}

	public Integer getInt() {
		try {
			List<Integer> ts = this.get();
			return ts != null && ts.size() > 0 ? (Integer) ts.get(0) : 0;
		} catch (Exception e) {
			return 0;
		}
	}
}
