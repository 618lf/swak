package com.tmt.database;

import javax.sql.DataSource;

import com.swak.common.persistence.incrementer.IdGen;
import com.tmt.common.MultiThreadTest;
import com.tmt.database.datasource.HikariDataSourceProvider;
import com.tmt.database.ops.MybatisOps;

/**
 * 数据库的性能测试
 * 数据库的查询基本在 20000 左右的性能
 * 数据库的插入性能基本在 2000 左右（20个连接）
 * 
 * DML 性能如下：
 * 事务表（主键基本每啥影响）：
 *    有主建： 9万条数据58秒
 *    无主建： 9万条数据55秒
 * 无事务表： 
 *    9万条数据3秒，基本和查询的性能一直
 * 数据库连接池：
 *    Druid 的性能没有 Hikari 的性能好
 *  
 * DQL性能如下：
 *    查询基本在 20000/s
 * @author lifeng
 */
public class TestMain {
	
	public static void main(String[] args) {
		
		// DataSourceProvider druiddataSourceProvider = new DruidProvider();
		DataSourceProvider hikariDataSourceProvider = new HikariDataSourceProvider();
		IdGen.setServerSn("server-1-1");
		DataSource dataSource = hikariDataSourceProvider.getDataSource();
		//final InsertOps jops = new JdbcOps(dataSource);
		
		// jdbc 的测试
		// 90 * 1000 平均 32s， 每秒 2821
		// 与数据库连接池有很大关系，设置的100 需要执行60秒
		// 基本上数据库连接池越大，效率高，但也有上线
//		MultiThreadTest.run(new Runnable() {
//			@Override
//			public void run() {
//				for(int i = 0; i< 90; i++) {
//					jops.insert();
//				}
//			}
//		}, 1000, "jdbc");
		
		final MybatisOps mops = new MybatisOps(dataSource);
		
		// mybatis的测试
		// 1000 * 90 (800) = 14s 基本和jdbc 差不多
		// 1000 * 90 (200) = 30s
		MultiThreadTest.run(new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i< 900; i++) {
					mops.insert();
				}
			}
		}, 100, "mybatis");
	}
}