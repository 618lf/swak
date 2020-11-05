package com.swak.async.persistence.tx;

import java.util.concurrent.atomic.AtomicBoolean;

import com.swak.async.persistence.execute.SqlSession;

/**
 * 事务变量
 * 
 * @author lifeng
 * @date 2020年10月5日 下午10:10:28
 */
class TransactionContextImmutability {

	/**
	 * Sql 操作Sessio
	 */
	SqlSession session;

	/**
	 * 只读 -- 只能用于查询
	 */
	boolean readOnly = false;

	/**
	 * 需要回滚的异常，默认仅仅运行时异常需要回滚
	 */
	Class<? extends Throwable>[] rollbackFor;

	/**
	 * 是否提交
	 */
	AtomicBoolean commited = new AtomicBoolean(false);
}
