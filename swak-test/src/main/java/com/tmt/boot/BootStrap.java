package com.tmt.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 启动服务
 * 
 * @author lifeng
 */
@RunWith(SpringJUnit4ClassRunner.class) // 使用junit4进行测试
@ContextConfiguration(locations = { "classpath:applicationContext.xml" }) // 加载配置文件
public class BootStrap {

	/**
	 * 启动服务器
	 */
	@Test
	public void start() {

		// 让服务器一直运行
		synchronized (BootStrap.class) {
			while (true) {
				try {
					BootStrap.class.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}