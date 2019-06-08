package com.swak.test.helloworld;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

public class Producer {

	public static void main(String[] args)
			throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
		DefaultMQProducer producer = new DefaultMQProducer("Pg");
		producer.setNamesrvAddr("127.0.0.1:9876");
		producer.start();

		Message message = new Message("swak-test", "tagA", "Hello Mq".getBytes());
		SendResult sendResult = producer.send(message);
		System.out.println(sendResult);

		producer.shutdown();
	}

}
