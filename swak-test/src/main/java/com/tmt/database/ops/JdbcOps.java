package com.tmt.database.ops;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.google.common.collect.Maps;
import com.swak.common.persistence.JdbcSqlExecutor;
import com.swak.common.persistence.incrementer.IdGen;
import com.tmt.database.InsertOps;
import com.tmt.database.QueryOps;
import com.tmt.database.entity.Shop;

public class JdbcOps implements InsertOps, QueryOps{
	
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
		param.put("ID", IdGen.id());
		param.put("NAME", "lifeng");
		JdbcSqlExecutor.insert("INSERT INTO SHOP_2(ID, NAME) VALUES(:ID, :NAME)", param);
	}
	
	public void insert1() {
		Map<String, Object> param = Maps.newHashMap();
		param.put("ID", IdGen.id());
		param.put("NAME", "lifeng");
		JdbcSqlExecutor.insert("INSERT INTO SHOP(ID, NAME) VALUES(:ID, :NAME)", param);
	}
	
	public void insert2() {
		Map<String, Object> param = Maps.newHashMap();
		param.put("ID", IdGen.id());
		param.put("NAME", "lifeng");
		JdbcSqlExecutor.insert("INSERT INTO SHOP_2(ID, NAME) VALUES(:ID, :NAME)", param);
	}
	
	public void insert3() {
		Map<String, Object> param = Maps.newHashMap();
		param.put("ID", IdGen.id());
		param.put("NAME", "lifeng");
		JdbcSqlExecutor.insert("INSERT INTO SHOP_3(ID, NAME) VALUES(:ID, :NAME)", param);
	}

	@Override
	public void query() {
		Map<String, Object> param = Maps.newHashMap();
		param.put("ID", IdGen.id());
		JdbcSqlExecutor.query("SELECT ID, NAME FROM SHOP WHERE ID = :ID", param, new RowMapper<Shop>(){
			@Override
			public Shop mapRow(ResultSet arg0, int arg1) throws SQLException {
				return null;
			}
		});
	}
}
