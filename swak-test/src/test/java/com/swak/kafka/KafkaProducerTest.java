package com.swak.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * 测试类
 * 
 * @author DELL
 */
public class KafkaProducerTest {

	public static KafkaProducer<String, String> producer() {
		Properties properties = new Properties();
		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.137.100:9092,192.168.137.100:9093");
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		return new KafkaProducer<>(properties);
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		// 创建 producer
		KafkaProducer<String, String> producer = producer();

		String[] goods = { "A", "B", "C" };
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {

				ProducerRecord<String, String> record = new ProducerRecord<String, String>("order", goods[j] + j,
						"NO." + i);

				Future<RecordMetadata> future = producer.send(record);

				// 阻塞式获取 -- 可以有异步的获取方式
				RecordMetadata meta = future.get();

				System.out.println("key=" + record.key() + ", val=" + record.value() + ", partition=" + meta.partition()
						+ ", offset=" + meta.offset());
			}
		}
	}
}
