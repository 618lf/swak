package com.tmt.web;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.swak.http.HttpServletResponse;
import com.swak.mvc.annotation.PathVariable;
import com.swak.mvc.annotation.RequestMapping;
import com.tmt.api.IHelloService;

/**
 * 所有的全部是json、xml、string
 * 
 * @author lifeng
 */
@Controller
@RequestMapping("/admin/shop/goods")
public class GoodsController {

	private AtomicInteger count = new AtomicInteger();

	/**
	 * 测试是否能自动注册服务
	 */
	@Autowired
	private IHelloService helloService;
	
	/**
	 * 获取总数
	 * 
	 * @return
	 */
	@RequestMapping("/count/{name}")
	public String count(@PathVariable Integer name, HttpServletResponse response) {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
		helloService.sayHello();
		return "获取总数";
	}

	/**
	 * 获取总数
	 * 
	 * @return
	 */
	@RequestMapping("/msg/{name}")
	public String count(@PathVariable String name) {
		return name;
	}

	/**
	 * 总数
	 * 
	 * @return
	 */
	@RequestMapping("/total")
	public Integer total() {
		return count.get();
	}
}