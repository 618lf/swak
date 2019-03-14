package com.tmt.shop.service;

import com.tmt.shop.entity.Foo;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;

/**
 * 发布服务
 * @author lifeng
 */
@MotanService
public class FooServiceImpl implements FooService {

	@Override
	public String hello(String name) {
		System.out.println(name);
		return "Hello " + name + "!";
	}

	@Override
	public Foo rename(Foo user, String name) {
		if (user == null) {
			System.out.println("user: null");
			return null;
		}
		System.out.println(user.getId() + " rename " + user.getName() + " to " + name);
		user.setName(name);
		return user;
	}
}