package com.swak.kafka;

import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.swak.utils.Lists;

/**
 * 测试类
 * 
 * @author DELL
 */
public class KafkaConsumerTest {

	public static KafkaConsumer<String, String> consumer() {
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.137.100:9092,192.168.137.100:9093");
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "Test02");

		// 第一次没有查询到此分组的offset时会设置一个值：看情况设置数据
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

		// 自动提交： offset 维护在kafka 的topic中 -- 默认是异步提交数据
		// 很容易导致数据的丢失和重复消费
		// 如果设置为不自动提交offset，需要手动提交，否则，会重复消费数据
		properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

		// 默认是5秒
		// properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, value);

		// 批量拉取数据的最大个数
		// properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "5");

		return new KafkaConsumer<>(properties);
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {

		// 创建 consumer
		KafkaConsumer<String, String> consumer = consumer();

		// 订阅主题
		consumer.subscribe(Lists.newArrayList("order"));

		// 获取数据： 每次获取0~多条数据
		while (true) {

			// 获取数据
			ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(0));

			// 获取的数据的条数
			if (records.count() > 0) {
				System.out.println("此次获取数据:" + records.count());
				records.forEach(record -> {
					System.out.println("key=" + record.key() + ", val=" + record.value() + ", partition="
							+ record.partition() + ", offset=" + record.offset());
				});

				// 手动提交offset
				consumer.commitSync();
			}
		}
	}
}
