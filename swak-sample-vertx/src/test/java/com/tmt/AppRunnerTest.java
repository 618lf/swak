package com.tmt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.swak.test.ApplicationTest;
import com.swak.vertx.annotation.VertxReferer;
import com.tmt.api.facade.GoodsServiceFacadeAsyncx;

/**
 * 系统测试的启动
 * 
 * @author lifeng
 */
@RunWith(SpringRunner.class)
@ApplicationTest
public class AppRunnerTest {
	
	@VertxReferer
	private GoodsServiceFacadeAsyncx goodsService;
	
	@Test
	public void contextLoads() {
		goodsService.sayHello();
	}
}
