package com.swak.rocketmq.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 订阅
 * 
 * @author lifeng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Subscribe {

	/**
	 * Consumers of the same role is required to have exactly same subscriptions and
	 * consumerGroup to correctly achieve load balance. It's required and needs to
	 * be globally unique.
	 *
	 *
	 * See <a href="http://rocketmq.apache.org/docs/core-concept/">here</a> for
	 * further discussion.
	 */
	String consumerGroup();

	/**
	 * Topic name.
	 */
	String topic();

	/**
	 * Control how to selector message.
	 *
	 * @see SelectorType
	 */
	SelectorType selectorType() default SelectorType.TAG;

	/**
	 * Control which message can be select. Grammar please see
	 * {@link SelectorType#TAG} and {@link SelectorType#SQL92}
	 */
	String selectorExpression() default "*";

	/**
	 * Control consume mode, you can choice receive message concurrently or orderly.
	 */
	ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;

	/**
	 * Control message mode, if you want all subscribers receive message all
	 * message, broadcasting is a good choice.
	 */
	MessageModel messageModel() default MessageModel.CLUSTERING;

	/**
	 * Max consumer thread number.
	 */
	int consumeThreadMax() default 64;
}