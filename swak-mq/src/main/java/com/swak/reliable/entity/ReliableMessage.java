package com.swak.reliable.entity;

import com.swak.entity.IdEntity;
import com.swak.reliable.enums.MessageState;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 可靠消息
 * 
 * @author lifeng
 * @date 2020年12月31日 下午5:47:16
 */
@Getter
@Setter
@Accessors(chain = true)
public class ReliableMessage extends IdEntity<Long> {

	private static final long serialVersionUID = 1L;

	/**
	 * 业务ID
	 */
	private Long transactionalId;
	private String exchange;
	private String routingKey;
	private String queue;
	private String message;

	/**
	 * 如果需要反馈则需要填写反馈地址： 消费者消费之后，回调值此地址来通知生产者处理
	 */
	private byte needAck;
	private String ackUrl;

	/**
	 * 状态值
	 */
	private MessageState state = MessageState.ready;

}