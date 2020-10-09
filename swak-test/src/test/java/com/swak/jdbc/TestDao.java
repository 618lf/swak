package com.swak.jdbc;

import org.junit.Test;

/**
 * 测试
 * 
 * @author lifeng
 * @date 2020年10月9日 下午9:45:18
 */
public class TestDao {

	@Test
	public void test1() {

		UserDao userDao = new UserDao();

		// 执行模型注册
		userDao.registerModel();

		// 输出解析的Sql
		System.out.println(userDao.toString());
	}
}
