package com.swak.reliable.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swak.reliable.ReliableMessageResult;
import com.swak.reliable.entity.ReliableMessage;

/**
 * 可靠消息服务
 * 
 * @author lifeng
 * @date 2020年12月31日 下午5:26:43
 */
@Service
public class ReliableMessageService {

	/**
	 * Sql 操作
	 */
	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	/**
	 * 开启事务并执行相关代码
	 * 
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Transactional
	public ReliableMessageResult handleInTransactional(ProceedingJoinPoint point) throws Throwable {

		// 执行增强
		Object result = point.proceed();

		// 保存可靠消息
		this.saveReliableMessage();

		// 返回结果
		return new ReliableMessageResult().setTransactionalResult(result);
	}

	/**
	 * 保存可靠消息
	 */
	@Transactional
	public void saveReliableMessage(ReliableMessage message) {

	}

	private void sendReliableMessage() {

	}
}