package com.tmt.shop.web;

import com.swak.motan.reactor.Reactor;
import com.swak.reactivex.web.annotation.RequestMapping;
import com.swak.reactivex.web.annotation.RestController;
import com.tmt.shop.service.FooService;
import com.tmt.shop.service.FooServiceAsync;
import com.weibo.api.motan.config.springsupport.annotation.MotanReferer;

import reactor.core.publisher.Mono;

/**
 * 测试的 demo
 * 
 * @author lifeng
 */
@RestController(path = "/admin/motan")
public class MotanController {

	@MotanReferer
	private FooService fooService;
	@MotanReferer
	private FooServiceAsync fooServiceAsync;

	/**
	 * 同步执行代码
	 * 
	 * @return
	 */
	@RequestMapping("say")
	public String say() {
		return fooService.hello("lifeng");
	}

	/**
	 * 异步执行代码，需要使用process 的功能，其实也可以手动生成这部分接口代码
	 * 
	 * @return
	 */
	@RequestMapping("say/async")
	public Mono<Object> asyncSay() {
		return Reactor.mono(fooServiceAsync.helloAsync("123"));
	}
}