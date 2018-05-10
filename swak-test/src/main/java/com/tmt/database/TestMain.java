package com.tmt.database;

import javax.sql.DataSource;

import com.swak.common.persistence.incrementer.IdGen;
import com.tmt.common.MultiThreadTest;
import com.tmt.database.datasource.DruidProvider;
import com.tmt.database.ops.MybatisOps;

/**
 * 数据库的性能测试
 * @author lifeng
 */
public class TestMain {
	
	public static void main(String[] args) {
		
		DataSourceProvider dataSourceProvider = new DruidProvider();
		IdGen.setServerSn("server-1-1");
		DataSource dataSource = dataSourceProvider.getDataSource();
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