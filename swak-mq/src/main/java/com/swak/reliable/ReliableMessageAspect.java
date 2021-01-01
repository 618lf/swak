package com.swak.reliable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.swak.reliable.service.ReliableMessageService;

/**
 * 可靠消息配置切面
 * 
 * @author lifeng
 * @date 2020年12月31日 下午5:16:07
 */
public class ReliableMessageAspect implements Ordered {

	@Autowired
	private ReliableMessageService reliableMessageService;

	/**
	 * 定义切入点
	 */
	@Pointcut(value = "@annotation(com.swak.reliable.ReliableMessage)")
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
	public <T> Object around(ProceedingJoinPoint point) throws Throwable {

		// 如果已经存在事务则抛出异常， 发送消息必须在事务之外，必然会导致消费者收到消息而事务还没提交
		boolean syschronizationActive = TransactionSynchronizationManager.isActualTransactionActive();
		if (syschronizationActive) {
			throw new ReliableMessageException("Dont Use Reliable Message in Tx!");
		}

		// 预留位置，将消息发送到可靠消息服务器，通过可靠消息服务器来保证消息的可靠性
		

		// 执行事务部分
		ReliableMessageResult result = this.reliableMessageService.handleInTransactional(point);

		// 发送可靠消息 -- 此时消息不一定要保证一定发送成功，但要保证消息是可溯性
		this.reliableMessageService.sendReliableMessage(result.getMessage());

		// 返回处理结果
		return result.getTransactionalResult();
	}

	/**
	 * Aop的顺序要早于spring的事务
	 * 注意Order序号要小于{@link org.springframework.aop.interceptor.ExposeInvocationInterceptor},
	 * PriorityOrdered.HIGHEST_PRECEDENCE + 1
	 */
	@Override
	public int getOrder() {
		return -1002;
	}
}
