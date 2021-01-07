package com.swak.reliable.service;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swak.reliable.ReliableMessage;
import com.swak.reliable.ReliableMessageException;
import com.swak.reliable.ReliableMessageResult;
import com.swak.reliable.dao.ReliableMessageDao;
import com.swak.reliable.entity.ReliableMessageVO;
import com.swak.reliable.invoke.InvokeService;
import com.swak.utils.JsonMapper;
import com.swak.utils.StringUtils;

/**
 * 可靠消息服务
 * 
 * @author lifeng
 * @date 2020年12月31日 下午5:26:43
 */
@Service
public class ReliableMessageService {

	@Autowired
	private ReliableMessageDao reliableMessageDao;
	private InvokeService invokeService = new InvokeService();

	/**
	 * 开启事务并执行相关代码
	 * 
	 * @param point
	 * @return
	 * @throws Throwable
	 */
	@Transactional
	public ReliableMessageResult handleInTransactional(ProceedingJoinPoint point) throws Throwable {

		// 获取可靠消息配置
		Method invokeMethod = this.getMethod(point);

		// 执行增强
		Object result = point.proceed();
		Object[] params = point.getArgs();

		// 创建可靠消息
		ReliableMessageVO message = this.buildMessage(invokeMethod, params, result);

		// 保存可靠消息
		this.saveReliableMessage(message);

		// 返回结果
		return new ReliableMessageResult().setTransactionalResult(result).setMessage(message);
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
	 * 创建可靠消息
	 * 
	 * @param context
	 * @param invokeResult
	 * @return
	 */
	private ReliableMessageVO buildMessage(Method invokeMethod, Object[] params, Object invokeResult) {

		// 可靠消息配置
		ReliableMessage context = invokeMethod.getAnnotation(ReliableMessage.class);

		// 消息
		ReliableMessageVO message = null;

		// 消息工厂
		Class<?>[] facs = context.messageFac();
		if (facs != null && facs.length > 0) {
			message = this.buildMessageUseFac(context, facs[0], invokeMethod, params);
		} else {
			message = this.buildMessageUseAnno(context, invokeResult);
		}
		return message;
	}

	/**
	 * 通过工厂创建消息
	 * 
	 * @param fac
	 * @param method
	 * @param invokeResult
	 * @return
	 */
	private ReliableMessageVO buildMessageUseFac(ReliableMessage context, Class<?> fac, Method invokeMethod,
			Object[] params) {
		Object result = null;
		try {
			result = invokeService.invoke(fac, invokeMethod, params);
		} catch (Exception e) {
			throw new ReliableMessageException("Incoke Fac Same Method Error", e);
		}

		return this.buildMessageUseAnno(context, result);
	}

	/**
	 * 通过注册配置创建消息
	 * 
	 * @param context
	 * @param invokeResult
	 * @return
	 */
	private ReliableMessageVO buildMessageUseAnno(ReliableMessage context, Object invokeResult) {
		ReliableMessageVO message = null;
		if (invokeResult == null) {
			throw new ReliableMessageException("Result Cannot Be Null.");
		}
		if (invokeResult instanceof ReliableMessageVO) {
			message = (ReliableMessageVO) invokeResult;
		} else {
			message = new ReliableMessageVO();
			message.setMessage(JsonMapper.toJson(invokeResult));
		}
		if (StringUtils.isBlank(message.getExchange())) {
			message.setExchange(context.exchange());
		}
		if (StringUtils.isBlank(message.getRoutingKey())) {
			message.setRoutingKey(context.routingKey());
		}
		if (StringUtils.isBlank(message.getQueue())) {
			message.setQueue(context.queue());
		}
		if (StringUtils.isBlank(message.getModule())) {
			message.setModule(context.module());
		}
		return message;
	}

	/**
	 * 保存可靠消息
	 */
	@Transactional
	public void saveReliableMessage(ReliableMessageVO message) {
		this.reliableMessageDao.insert(message);
	}

	/**
	 * 发送可靠消息
	 */
	public void sendReliableMessage(ReliableMessageVO message) {

	}
}