package com.tmt.shop.web;

import com.swak.reactivex.web.annotation.RequestMapping;
import com.swak.reactivex.web.annotation.RestController;
import com.tmt.shop.service.FooService;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;

/**
 * 测试的 demo
 * 
 * @author lifeng
 */
@RestController("/admin/motan")
public class MotanController {

	@MotanReferer
	private FooService fooService;

	@RequestMapping("say")
	public String say() {
		return fooService.hello("lifeng");
	}
}