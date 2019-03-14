package com.swak.config.jdbc.database;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;
import com.swak.utils.StringUtils;

/**
 * 数据库的配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.DATASOURCE_PREFIX)
public class DataSourceProperties {

	private String name = "SWAK - DB";
	private Database db = Database.mysql;
	private String url;
	private String username;
	private String password;
	private String driverClassName;
	private Integer initialSize = 10;
	private Integer minIdle = 10;
	private Integer maxActive = 20; // 连接池中允许的最大连接数。缺省值：10；推荐的公式：((core_count * 2) + effective_spindle_count)
	private Integer maxWait = 60000; // 获取连接时最大等待时间，单位毫秒。
	private Integer timeBetweenEvictionRunsMillis = 60000; // 多久检查一次
	private Integer minEvictableIdleTimeMillis = 300000; // 最小的生存时间
	private String validationQuery = "SELECT 1";
	private Boolean testWhileIdle = true;
	private Boolean testOnBorrow = false;
	private Boolean testOnReturn = false;
	private Boolean poolPreparedStatements = true;
	private Integer maxPoolPreparedStatementPerConnectionSize = 20;
	private String filters = "stat, slf4j";

	private Integer prepStmtCacheSize = 250;
	private Integer prepStmtCacheSqlLimit = 2048;
	private Integer maxLifetime = 1800000; // 一个连接的生命时长（毫秒），超时而且没被使用则被释放（retired），缺省:30分钟，建议设置比数据库超时时长少30秒

	public DataSourceProperties() {
	}

	public DataSourceProperties(DataSourceProperties copy) {
		this.name = copy.getName();
		this.db = copy.getDb();
		this.url = copy.getUrl();
		this.username = copy.getUsername();
		this.password = copy.getPassword();
		this.driverClassName = copy.getDriverClassName();
		this.initialSize = copy.getInitialSize();
		this.minIdle = copy.getMinIdle();
		this.maxActive = copy.getMaxActive();
		this.maxWait = copy.getMaxWait();
		this.timeBetweenEvictionRunsMillis = copy.getTimeBetweenEvictionRunsMillis();
		this.minEvictableIdleTimeMillis = copy.getMinEvictableIdleTimeMillis();
		this.validationQuery = copy.getValidationQuery();
		this.testWhileIdle = copy.getTestWhileIdle();
		this.testOnBorrow = copy.getTestOnBorrow();
		this.testOnReturn = copy.getTestOnReturn();
		this.poolPreparedStatements = copy.getPoolPreparedStatements();
		this.maxPoolPreparedStatementPerConnectionSize = copy.getMaxPoolPreparedStatementPerConnectionSize();
		this.filters = copy.getFilters();
		this.prepStmtCacheSize = copy.getPrepStmtCacheSize();
		this.prepStmtCacheSqlLimit = copy.getPrepStmtCacheSqlLimit();
		this.maxLifetime = copy.getMaxLifetime();
	}

	public Database getDb() {
		return db;
	}

	public void setDb(Database db) {
		this.db = db;
	}

	public Integer getMaxLifetime() {
		return maxLifetime;
	}

	public void setMaxLifetime(Integer maxLifetime) {
		this.maxLifetime = maxLifetime;
	}

	public Integer getPrepStmtCacheSize() {
		return prepStmtCacheSize;
	}

	public void setPrepStmtCacheSize(Integer prepStmtCacheSize) {
		this.prepStmtCacheSize = prepStmtCacheSize;
	}

	public Integer getPrepStmtCacheSqlLimit() {
		return prepStmtCacheSqlLimit;
	}

	public void setPrepStmtCacheSqlLimit(Integer prepStmtCacheSqlLimit) {
		this.prepStmtCacheSqlLimit = prepStmtCacheSqlLimit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 自动适配驱动
	 * 
	 * @return
	 */
	public String getDriverClassName() {
		this.driverFit();
		return driverClassName;
	}

	public void driverFit() {
		if (!StringUtils.isBlank(driverClassName) || db == null) {
			return;
		}
		if (db == Database.mysql || db == Database.sharding) {
			this.driverFitMysql();
		} else if (db == Database.h2) {
			this.driverClassName = "org.h2.Driver";
		}
	}

	// mysql 驱动的自动发现
	private void driverFitMysql() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			this.driverClassName = "com.mysql.cj.jdbc.Driver";
		} catch (ClassNotFoundException e) {
		}
		try {
			Class.forName("com.mysql.jdbc.Driver");
			this.driverClassName = "com.mysql.jdbc.Driver";
		} catch (ClassNotFoundException e) {
		}
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public Integer getInitialSize() {
		return initialSize;
	}

	public void setInitialSize(Integer initialSize) {
		this.initialSize = initialSize;
	}

	public Integer getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(Integer minIdle) {
		this.minIdle = minIdle;
	}

	public Integer getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}

	public Integer getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(Integer maxWait) {
		this.maxWait = maxWait;
	}

	public Integer getTimeBetweenEvictionRunsMillis() {
		return timeBetweenEvictionRunsMillis;
	}

	public void setTimeBetweenEvictionRunsMillis(Integer timeBetweenEvictionRunsMillis) {
		this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
	}

	public Integer getMinEvictableIdleTimeMillis() {
		return minEvictableIdleTimeMillis;
	}

	public void setMinEvictableIdleTimeMillis(Integer minEvictableIdleTimeMillis) {
		this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
	}

	public String getValidationQuery() {
		return validationQuery;
	}

	public void setValidationQuery(String validationQuery) {
		this.validationQuery = validationQuery;
	}

	public Boolean getTestWhileIdle() {
		return testWhileIdle;
	}

	public void setTestWhileIdle(Boolean testWhileIdle) {
		this.testWhileIdle = testWhileIdle;
	}

	public Boolean getTestOnBorrow() {
		return testOnBorrow;
	}

	public void setTestOnBorrow(Boolean testOnBorrow) {
		this.testOnBorrow = testOnBorrow;
	}

	public Boolean getTestOnReturn() {
		return testOnReturn;
	}

	public void setTestOnReturn(Boolean testOnReturn) {
		this.testOnReturn = testOnReturn;
	}

	public Boolean getPoolPreparedStatements() {
		return poolPreparedStatements;
	}

	public void setPoolPreparedStatements(Boolean poolPreparedStatements) {
		this.poolPreparedStatements = poolPreparedStatements;
	}

	public Integer getMaxPoolPreparedStatementPerConnectionSize() {
		return maxPoolPreparedStatementPerConnectionSize;
	}

	public void setMaxPoolPreparedStatementPerConnectionSize(Integer maxPoolPreparedStatementPerConnectionSize) {
		this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}
}