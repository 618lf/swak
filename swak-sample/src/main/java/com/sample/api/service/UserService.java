package com.sample.api.service;

import com.sample.api.entity.User;
import com.sample.api.facade.UserServiceFacade;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;

@MotanService
public class UserService implements UserServiceFacade {

	@Override
	public User getUser() {
		return new User();
	}
}
