package com.swak.async.persistence.sqls;

import java.util.List;

import com.swak.async.persistence.RowMapper;
import com.swak.async.persistence.SqlParam;
import com.swak.entity.Parameters;
import com.swak.persistence.dialect.Dialect;

public class PageSql<T> extends ShardingSql<T> {

	Dialect dialect;
	QuerySql<T> querySql;
	Parameters pageParam;

	public PageSql(QuerySql<T> querySql, Dialect dialect, Parameters param) {
		super(querySql.table);
		this.querySql = querySql;
		this.dialect = dialect;
		this.pageParam = param;
	}

	@Override
	public String parseScript(SqlParam<T> param) {
		int pageNum = pageParam.getPageIndex();
		int pageSize = pageParam.getPageSize();
		int recordCount = pageParam.getRecordCount();
		int pageCount = getPageCount(recordCount, pageSize);
		if (pageNum > pageCount) {
			pageNum = pageCount;
		}
		return dialect.getLimitString(querySql.parseScript(param), (pageNum - 1) * pageSize, pageSize);
	}

	@Override
	public List<Object> parseParams(SqlParam<T> param) {
		return querySql.parseParams(param);
	}

	@Override
	public <U> RowMapper<U> rowMap() {
		return querySql.rowMap();
	}

	/**
	 * page count
	 * 
	 * @param recordCount
	 * @param pageSize
	 * @return
	 */
	private int getPageCount(int recordCount, int pageSize) {
		if (recordCount == 0)
			return 0;
		return recordCount % pageSize > 0 ? ((recordCount / pageSize) + 1) : (recordCount / pageSize);
	}
}
