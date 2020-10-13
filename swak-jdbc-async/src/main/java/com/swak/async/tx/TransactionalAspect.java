package com.swak.async.tx;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import com.swak.async.execute.SqlExecuter;

/**
 * 事务配置切面
 * 
 * @author lifeng
 * @date 2020年10月8日 下午9:52:24
 */
public class TransactionalAspect implements Ordered {

	private Logger logger = LoggerFactory.getLogger(TransactionalAspect.class);

	@Autowired
	private SqlExecuter sqlExecuter;

	/**
	 * 定义切入点
	 */
	@Pointcut(value = "@annotation(com.swak.async.tx.Transactional)")
	private void cut() {
	}

	/**
	 * 定义增强
	 * 
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Around("cut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		TransactionContext context = null;
		try {
			context = this.beginTransactional(point);
			Object result = point.proceed();
			return this.endTransactional(context, result);
		} catch (Exception e) {
			if (context != null) {
				context.finish(e);
			}
			logger.error("执行异步事务过程中出现错误：", e);
			throw e;
		}
	}

	/**
	 * 开启事务，会继续上一次的事务
	 * 
	 * @param point
	 */
	private TransactionContext beginTransactional(ProceedingJoinPoint point) {
		TransactionContext context = null;
		Transactional transaction = this.getMethod(point).getAnnotation(Transactional.class);
		Object[] args = point.getArgs();
		if (transaction != null && args != null && args.length >= 1 && args[0] instanceof TransactionContext) {
			context = (TransactionContext) args[0];
			context = transaction.readOnly() ? sqlExecuter.beginQuery(context) : sqlExecuter.beginTransaction(context);
			args[0] = context.acquire().setRollbackFor(transaction.rollbackFor());
		}
		return context;
	}

	/**
	 * 结束事务
	 * 
	 * @param point
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object endTransactional(TransactionContext context, Object result) {
		if (context != null && context.released()) {
			if (result instanceof CompletionStage) {
				CompletableFuture<Object> future = new CompletableFuture<>();
				((CompletionStage) result).whenComplete((o, e) -> {
					this.finishTransactional(o, (Throwable) e, context, future);
				});
				return future;
			}
		}
		return result;
	}

	/**
	 * 结束事务
	 * 
	 * @param o
	 * @param e
	 * @param context
	 * @param future
	 */
	private void finishTransactional(Object o, Throwable e, TransactionContext context,
			CompletableFuture<Object> future) {
		context.finish(e).whenComplete((o1, e1) -> {
			if (e1 != null) {
				future.completeExceptionally(e1);
			} else {
				future.complete(o);
			}
		});
	}

	/**
	 * 获得当前的 Method
	 * 
	 * @param point
	 * @return
	 */
	private Method getMethod(ProceedingJoinPoint point) {
		Signature s = point.getSignature();
		MethodSignature ms = (MethodSignature) s;
		return ms.getMethod();
	}

	/**
	 * Aop的顺序要早于spring的事务
	 * 注意Order序号要小于{@link org.springframework.aop.interceptor.ExposeInvocationInterceptor},
	 * PriorityOrdered.HIGHEST_PRECEDENCE + 1
	 */
	@Override
	public int getOrder() {
		return -1001;
	}
}
