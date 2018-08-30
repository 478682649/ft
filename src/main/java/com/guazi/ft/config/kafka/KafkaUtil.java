package com.guazi.ft.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaUtil
 *
 * @author shichunyang
 */
public class KafkaUtil {

	/**
	 * 生产者配置
	 *
	 * @param servers 服务地址
	 */
	public static Map<String, Object> producerConfig(String servers) {
		Map<String, Object> producer = new HashMap<>(16);

		// 服务地址
		producer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
		// 等待服务器所有副本的确认信息
		producer.put(ProducerConfig.ACKS_CONFIG, "all");
		// 重试次数
		producer.put(ProducerConfig.RETRIES_CONFIG, 10);
		// 批量发送消息的数量
		producer.put(ProducerConfig.BATCH_SIZE_CONFIG, 100);
		// 及时发送
		producer.put(ProducerConfig.LINGER_MS_CONFIG, 0);
		// 用作缓存的字节数
		producer.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 2048000);
		// 保证消息有序
		producer.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);
		// key序列化
		producer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		// value序列化
		producer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		return producer;
	}

	/**
	 * 根据生产者配置获取KafkaTemplate
	 *
	 * @param producer 生产者配置
	 * @return KafkaTemplate
	 */
	public static KafkaTemplate<String, String> getKafkaTemplate(Map<String, Object> producer) {
		return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(producer));
	}

	/**
	 * 消费者配置
	 *
	 * @param servers 服务地址
	 * @param groupId 群组id
	 * @return 消费者配置信息
	 */
	public static Map<String, Object> consumerConfig(String servers, String groupId) {
		Map<String, Object> consumer = new HashMap<>(16);

		// 服务地址
		consumer.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
		// 分组id
		consumer.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		// 设置offset自动提交
		consumer.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
		// 自动提交offset的频率(毫秒)
		consumer.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, 1000);
		// 检测消费者失败的超时时间
		consumer.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
		// key反序列化
		consumer.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		// value反序列化
		consumer.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		// 从最早的offset开始消费
		consumer.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		// 一次poll记录的数量
		consumer.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);

		return consumer;
	}

	/**
	 * 根据消费者得到kafka监听类
	 *
	 * @param consumer 消费者
	 * @return 监听类
	 */
	public static ConcurrentKafkaListenerContainerFactory<String, String> getConcurrentKafkaListenerContainerFactory(Map<String, Object> consumer) {
		ConcurrentKafkaListenerContainerFactory<String, String> concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
		concurrentKafkaListenerContainerFactory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(consumer));
		return concurrentKafkaListenerContainerFactory;
	}
}
