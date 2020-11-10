package com.swak.country;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.swak.config.jdbc.database.DataSourceProperties;
import com.swak.config.jdbc.database.HikariDataSourceAutoConfiguration;
import com.swak.utils.Maps;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 数据库校验
 * 
 * @author lifeng
 * @date 2020年9月1日 下午5:06:32
 */
public class DbCheckTest {

	/**
	 * 初始化操作模板
	 * 
	 * @return
	 */
	public static NamedParameterJdbcTemplate initDataSource() {
		DataSourceProperties properties = new DataSourceProperties();
		properties.setUrl(
				"jdbc:mysql://127.0.0.1:3306/cloud?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8");
		properties.setUsername("root");
		properties.setPassword("rootadmin");
		DataSource dataSource = new HikariDataSourceAutoConfiguration().hikariDataSource(properties, null);
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		return new NamedParameterJdbcTemplate(jdbcTemplate);
	}

	public static void main(String[] args) throws IOException {

		// 初始化数据库服务
		OecdService oecdService = new OecdService(initDataSource());

		System.out.println("====== 是否在数据库中都存在： ======");
		// 所有协定的国家
		List<Country> countrys = OecdCountryTest.countrys();
		for (Country country : countrys) {
			Oecd oecd = oecdService.get(country.address);
			if (oecd == null) {
				System.out.println(country);
			}
		}

		System.out.println("====== 是否在文件中都存在： ======");

		// 数据库中
		List<Oecd> oecds = oecdService.getAll();
		for (Oecd oecd : oecds) {
			boolean found = false;
			for (Country country : countrys) {
				if (oecd.name.equals(country.address)) {
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println(oecd);
			}
		}

		System.out.println("====== 协定国家是否在国家列表中都存在： ======");
		countrys = CountryTest.countrys();
		for (Oecd oecd : oecds) {
			boolean found = false;
			for (Country country : countrys) {
				if (oecd.name.equals(country.address)) {
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println(oecd);
			}
		}
	}
}

@ToString
@Getter
@Setter
class Oecd {
	Long id;
	String name;
}

class OecdService {
	private NamedParameterJdbcTemplate jdbcTemplate;

	public OecdService(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Oecd get(String name) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("NAME", name);
		List<Oecd> datas = jdbcTemplate.query("SELECT ID, NAME FROM NONR_AGREEMENT_COUNTRY WHERE NAME=:NAME", param,
				getRowMapper());
		return datas != null && datas.size() >= 1 ? datas.get(0) : null;
	}

	public List<Oecd> getAll() {
		Map<String, Object> param = Maps.newHashMap();
		List<Oecd> datas = jdbcTemplate.query("SELECT ID, NAME FROM NONR_AGREEMENT_COUNTRY", param, getRowMapper());
		return datas;
	}

	/**
	 * 数据转换
	 * 
	 * @return
	 */
	private RowMapper<Oecd> getRowMapper() {
		return (rs, num) -> {
			Oecd gyro = new Oecd();
			gyro.setId(rs.getLong("ID"));
			gyro.setName(rs.getString("NAME"));
			return gyro;
		};
	}
}
