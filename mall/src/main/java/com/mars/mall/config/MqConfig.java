package com.mars.mall.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 队列声明配置，确保消费者启动时队列已存在。
 */
@Configuration
public class MqConfig {

    /**
     * 声明 payNotify 队列（持久化、非自动删除）。
     */
    @Bean
    public Queue payNotifyQueue() {
        return new Queue("payNotify", true, false, false);
    }

    /**
     * 显式注册 RabbitAdmin，应用启动时自动声明队列/交换机/绑定。
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}