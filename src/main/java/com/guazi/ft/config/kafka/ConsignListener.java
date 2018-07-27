package com.guazi.ft.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * ConsignListener
 *
 * @author shichunyang
 */
public class ConsignListener {
    @KafkaListener(topics = {"${kafka.consign.topic}"}, containerFactory = "consignConcurrentKafkaListenerContainerFactory", groupId = "${kafka.consign.groupId}")
    public void listen(ConsumerRecord<String, String> record) {
        System.out.println("kafka key==>" + record.key() + ", value==>" + record.value());
    }
}
