package com.guazi.ft.config.kafka;

import com.guazi.ft.aop.ControllerAspect;
import com.guazi.ft.common.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * ConsignListener
 *
 * @author shichunyang
 */
@Slf4j
public class ConsignListener {
	@KafkaListener(topics = {"${kafka.consign.topic}"}, containerFactory = "consignConcurrentKafkaListenerContainerFactory", groupId = "${kafka.consign.groupId}")
	public void listen(ConsumerRecord<String, String> record) {
		MDC.put(ControllerAspect.REQUEST_ID, CommonUtil.get32UUID());
		log.info("kafka key==>{}, value==>{}", record.key(), record.value());
		log.info("kafka key==>{}, value==>{}", record.key(), record.value());
		log.info("kafka consumer finish");
	}
}
