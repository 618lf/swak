package com.swak.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;

/**
 * 测试类
 * 
 * @author DELL
 */
public class KafkaTest {

	public void producer() {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.137");
		KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
	}

	public static void main(String[] args) {

	}
}
