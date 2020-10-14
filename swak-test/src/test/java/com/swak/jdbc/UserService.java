package com.swak.jdbc;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.async.persistence.BaseDao;
import com.swak.async.service.BaseService;

class UserService extends BaseService<User, Long> {

	@Autowired
	UserDao userDao;

	/**
	 * 其他服务
	 */
	@Autowired
	MemberService memberService;

	@Override
	protected BaseDao<User, Long> getBaseDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * 编程式事务的查询后保存
	 * 
	 * @return
	 */
	public CompletableFuture<Void> save() {
		return this.beginTransaction().txCompose((context) -> {
			User user = new User();
			user.setId(0L);
			return this.get(context, user);
		}).txCompose(context -> {
			User user = context.getValue();
			user.setName("税务公社");
			return this.update(context, user);
		}).finish(context -> {
			return context.getValue();
		});
	}
}
