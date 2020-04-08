package com.tmt.api.service;

import com.tmt.api.entity.User;
import com.tmt.api.facade.UserServiceFacade;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;

@MotanService
public class UserService implements UserServiceFacade {

	@Override
	public User getUser() {
		return new User();
	}
}
