package com.swak.jdbc;

import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

import com.swak.async.datasource.DataSource;
import com.swak.async.execute.SqlExecuter;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

/**
 * 测试
 * 
 * @author lifeng
 * @date 2020年10月9日 下午9:45:18
 */
public class TestDao {

	SqlExecuter sqlExecuter;
	UserDao userDao;
	UserService userService;

	/**
	 * 开始
	 */
	@Before
	public void init() {
		
		System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
		
		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("192.168.137.100")
				.setDatabase("cloud").setUser("root").setPassword("rootadmin");

		// Pool options
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

		// Create the client pool
		MySQLPool client = MySQLPool.pool(connectOptions, poolOptions);

		// 执行器
		sqlExecuter = new SqlExecuter(new DataSource(client));
		userDao = new UserDao();
		userDao.setSqlExecuter(sqlExecuter);
		userDao.registerModel();
		userService = new UserService();
		userService.setUserDao(userDao);
	}

	@Test
	public void get() throws InterruptedException {
		User user = new User();
		user.setId(1L);
		userService.get(user).thenAccept(r -> {
			System.out.println("结果：" + (r != null ? r.getName() : "~无数据"));
		});
		new CountDownLatch(1).await();
	}

	@Test
	public void update() throws InterruptedException {
		User user = new User();
		user.setId(1L);
		userService.save().whenComplete((r, e) -> {
			if (e != null) {
				e.printStackTrace();
			}
			System.out.println("执行结束");
		});
		new CountDownLatch(1).await();
	}
}
