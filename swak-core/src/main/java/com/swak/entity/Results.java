package com.swak.entity;

import java.util.function.Function;

/**
 * 配合异步使用
 * 
 * @author lifeng
 */
public interface Results {

	static <T> Function<T, Result> identity() {
		return t -> Result.success(t);
	}
}
