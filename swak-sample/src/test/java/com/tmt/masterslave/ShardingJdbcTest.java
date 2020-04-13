package com.tmt.masterslave;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sample.api.entity.Goods;
import com.sample.api.facade.GoodsServiceFacade;
import com.tmt.AppRunnerTest;

/**
 * 使用 ShardingJdbc 测试读写分离
 * 
 * @author lifeng
 */
public class ShardingJdbcTest extends AppRunnerTest {

	@Autowired
	private GoodsServiceFacade goodsService;

	/**
	 * 查询
	 */
	@Test
	public void get_goods() {
		System.out.println("直接获取数据：");
		Goods goods = goodsService.get();
		System.out.println(goods);
	}

	/**
	 * 保存
	 */
	@Test
	public void save_goods() {
		System.out.println("保存数据：");
		Goods goods = goodsService.save();
		System.out.println(goods);
	}

	/**
	 * 先获取再保存
	 */
	@Test
	public void get_save_goods() {
		System.out.println("先获取再保存：");
		Goods goods = goodsService.get_save();
		System.out.println(goods);
	}

	/**
	 * 先获取再保存再获取
	 */
	@Test
	public void get_save_get_goods() {
		System.out.println("先获取再保存再获取：");
		Goods goods = goodsService.get_save_get();
		System.out.println(goods);
	}
	
	/**
	 * 强制走主库
	 */
	@Test
	public void hint_get_save_get() {
		System.out.println("强制走主库：");
		Goods goods = goodsService.hint_get_save_get();
		System.out.println(goods);
	}
}
