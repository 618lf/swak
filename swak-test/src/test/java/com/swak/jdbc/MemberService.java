package com.swak.jdbc;

import java.util.concurrent.CompletableFuture;

import com.swak.async.persistence.BaseDao;
import com.swak.async.service.BaseService;
import com.swak.async.tx.TransactionContext;
import com.swak.async.tx.Transactional;

public class MemberService extends BaseService<User, Long> {

	UserDao userDao;

	@Override
	protected BaseDao<User, Long> getBaseDao() {
		return userDao;
	}

	/**
	 * 保存
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
	 * 使用注解 来管理事务
	 * 
	 * @param ctx
	 * @param user
	 * @return
	 */
	@Transactional
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
		}).thenApply(context -> null);
	}
}
