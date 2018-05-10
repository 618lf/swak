package com.tmt.database.ops;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.google.common.collect.Maps;
import com.swak.common.persistence.JdbcSqlExecutor;
import com.tmt.database.InsertOps;

public class JdbcOps implements InsertOps{
	
	private final DataSource dataSource;
	private final JdbcProperties properties;
	
	public JdbcOps(DataSource dataSource) {
		this.dataSource = dataSource;
		this.properties = new JdbcProperties();
		jdbcTemplate();
	}
	
	public JdbcTemplate jdbcTemplate() {
		JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
		JdbcProperties.Template template = this.properties.getTemplate();
		jdbcTemplate.setFetchSize(template.getFetchSize());
		jdbcTemplate.setMaxRows(template.getMaxRows());
		if (template.getQueryTimeout() != null) {
			jdbcTemplate.setQueryTimeout((int) template.getQueryTimeout().getSeconds());
		}
		JdbcSqlExecutor.setJdbcTemplate(new NamedParameterJdbcTemplate(jdbcTemplate));
		return jdbcTemplate;
	}

	@Override
	public void insert() {
		Map<String, Object> param = Maps.newHashMap();
		param.put("ID", 1);
		param.put("NAME", "lifeng");
		JdbcSqlExecutor.insert("INSERT INTO SHOP_2(ID, NAME) VALUES(:ID, :NAME)", param);
	}
}
