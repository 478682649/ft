package com.guazi.ft.config.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

/**
 * RabbitConfig
 *
 * @author shichunyang
 */
@Slf4j
//@Configuration
public class RabbitConfig {

    @Bean("rabbitConnectionFactory")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses("127.0.0.1");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");

        // 支持发布确认
        connectionFactory.setPublisherConfirms(true);
        // 支持发布返回
        connectionFactory.setPublisherReturns(true);

        return connectionFactory;
    }

    @Bean("ftRabbitTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("rabbitConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        template.setEncoding("UTF-8");
        template.setMandatory(true);
        template.setConfirmCallback((correlationData, ack, cause) -> {
            log.info("");
        });
        return template;
    }

    @Bean("rabbitExchange")
    public Exchange exchange() {
        return ExchangeBuilder.directExchange("ft.direct.exchange").durable(true).build();
    }

    @Bean("rabbitQueue")
    public Queue queue() {
        return QueueBuilder.durable("ft.queue.q1").build();
    }

    @Bean
    public Binding binding(@Qualifier("rabbitExchange") Exchange exchange, @Qualifier("rabbitQueue") Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with("q1").noargs();
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer(
            @Qualifier("rabbitConnectionFactory") ConnectionFactory connectionFactory,
            @Qualifier("rabbitQueue") Queue queue

    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue);
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            byte[] body = message.getBody();
            System.out.println("receive==>" + new String(body));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        });
        return container;
    }
}
