package com.swak.reliable;

import com.swak.rabbit.message.Message;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ReliableMessageResult {

	private Object transactionalResult;
	private Message message;
}
