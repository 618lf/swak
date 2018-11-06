package com.swak.config.jdbc.database;

import org.springframework.boot.jdbc.metadata.AbstractDataSourcePoolMetadata;
import org.sqlite.SQLiteDataSource;

public class SqlLiteDataSourcePoolMetadata extends AbstractDataSourcePoolMetadata<SQLiteDataSource> {

	public SqlLiteDataSourcePoolMetadata(SQLiteDataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Integer getActive() {
		return -1;
	}

	@Override
	public Integer getMax() {
		return -1;
	}

	@Override
	public Integer getMin() {
		return -1;
	}

	@Override
	public String getValidationQuery() {
		return "";
	}

	@Override
	public Boolean getDefaultAutoCommit() {
		return true;
	}
}