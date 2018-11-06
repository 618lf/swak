package com.swak.persistence.dialect;

/**
 * 方言
 * 
 * @author lifeng
 */
public interface Dialect {

	public boolean supportsLimit();

	public String getLimitString(String sql, boolean hasOffset);

	public String getLimitString(String sql, int offset, int limit);
}