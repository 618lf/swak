package com.swak.jdbc;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.async.persistence.BaseDao;
import com.swak.async.service.BaseService;
import com.swak.async.tx.TransactionContext;
import com.swak.async.tx.Transactional;

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

	/**
	 * 手动开启事务并结束事务
	 * 
	 * @return
	 */
	public CompletableFuture<Long> save(User user) {
		return this.beginTransaction().txCompose(context -> {
			return this.insert(context, user);
		}).txCompose(context -> {
			return this.update(context, user);
		}).finish(context -> null);
	}

	/**
	 * 使用注解 来管理事务, 可以配置只读和回滚的异常
	 * 
	 * @param ctx
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public CompletableFuture<Long> saveUseAnno1(TransactionContext ctx, User user) {
		return ctx.toFuture().txCompose(context -> {
			return this.insert(context, user);
		}).txCompose(context -> {
			return this.update(context, user);
		}).thenCompose(context -> this.saveUseAnno2(context, user));
	}

	/**
	 * 使用注解 来管理事务 -- 可以将多个事务方法串在一起
	 * 
	 * @param ctx
	 * @param user
	 * @return
	 */
	@Transactional
	public CompletableFuture<Long> saveUseAnno2(TransactionContext ctx, User user) {
		return ctx.toFuture().txCompose(context -> {
			return this.insert(context, user);
		}).txCompose(context -> {
			return this.update(context, user);
		}).thenCompose(context -> memberService.saveUseAnno1(context, user));
	}
}
