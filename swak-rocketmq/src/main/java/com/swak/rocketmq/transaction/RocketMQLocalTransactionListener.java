package com.swak.rocketmq.transaction;

import com.swak.rocketmq.message.Message;

public interface RocketMQLocalTransactionListener {

	RocketMQLocalTransactionState executeLocalTransaction(final Message msg, final Object arg);

	RocketMQLocalTransactionState checkLocalTransaction(final Message msg);
}
