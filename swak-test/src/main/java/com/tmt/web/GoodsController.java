package com.tmt.web;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Controller;

import com.swak.http.HttpServletResponse;
import com.swak.mvc.annotation.PathVariable;
import com.swak.mvc.annotation.RequestMapping;

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
	 * 获取总数
	 * 
	 * @return
	 */
	@RequestMapping("/count/{name}")
	public String count(@PathVariable Integer name, HttpServletResponse response) {
		for(int i=0; i<5000000;i++) {}
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