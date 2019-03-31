package com.tmt.shop.service;

import com.swak.flux.verticle.async.FluxAsync;
import com.tmt.shop.entity.Foo;
import com.weibo.api.motan.transport.async.MotanAsync;

/**
 * 定义服务
 * @author lifeng
 */
@MotanAsync
@FluxAsync
public interface FooService {
	
	String hello(String name);

	Foo rename(Foo user, String name);
}