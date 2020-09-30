package com.swak.jdbc;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

/**
 * 测试异步 SQL 的操作
 * 
 * @author lifeng
 * @date 2020年9月30日 下午3:03:44
 */
public class TestMain {

	public static void main(String[] args) {

		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("192.168.137.100")
				.setDatabase("cloud").setUser("root").setPassword("rootadmin");

		// Pool options
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

		// Create the client pool
		MySQLPool client = MySQLPool.pool(connectOptions, poolOptions);

		// A simple query
		client.query("SELECT * FROM cloud_user").execute(ar -> {
			if (ar.succeeded()) {
				RowSet<Row> result = ar.result();
				System.out.println("Got " + result.size() + " rows ");
			} else {
				System.out.println("Failure: " + ar.cause().getMessage());
			}

			// Now close the pool
			client.close();
		});
	}
}
