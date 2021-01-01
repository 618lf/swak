package com.swak.reliable;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.swak.utils.StringUtils;

/**
 * 可靠消息，两种方式创建可靠消息：<br>
 * 1. 根据方法的返回值，会将方法的返回值作为消息，所有需要将路由等信息填入<br>
 * 2. 使用消息创建工厂，这种方式比较灵活，会调用指定类的同名方法且将返回值作为参数传递<br>
 * 优先使用第二种方式， 例如： <br>
 * 
 * UserServiceImpl.class<br>
 * 
 * @ReliableMessage(messageFac = UserEventService.class) <br>
 *                             public User register(UserAccount account) {return
 *                             User;} <br>
 * 
 *                             UserEventService.class <br>
 *                             public void register(User user) {} <br>
 * 
 * 
 * @author lifeng
 * @date 2020年12月31日 下午5:13:15
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ReliableMessage {

	/**
	 * 交换机
	 * 
	 * @return
	 */
	String exchange() default StringUtils.EMPTY;

	/**
	 * 路由规则
	 * 
	 * @return
	 */
	String routingKey() default StringUtils.EMPTY;

	/**
	 * 队列
	 * 
	 * @return
	 */
	String queue() default StringUtils.EMPTY;

	/**
	 * 模块
	 * 
	 * @return
	 */
	String module() default StringUtils.EMPTY;

	/**
	 * 是否存储消息：如果不存储则消息需要是可追溯的如果发送失败可以更具可追溯性重建消息
	 * 
	 * @return
	 */
	boolean storeMessage() default true;

	/**
	 * 可靠消息的创建工厂
	 * 
	 * @return
	 */
	Class<?>[] messageFac() default {};
}