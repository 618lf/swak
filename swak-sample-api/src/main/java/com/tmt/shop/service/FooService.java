package com.tmt.shop.service;

import com.tmt.shop.entity.Foo;

/**
 * 定义服务
 * @author lifeng
 */
public interface FooService {
	
	String hello(String name);

	Foo rename(Foo user, String name);
}
