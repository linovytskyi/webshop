package com.example.deliveryservice.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String ORDER_PAID_TOPIC = "order.paid";
    public static final String DELIVERY_CREATED_TOPIC = "delivery.created";
    public static final String DELIVERY_SERVICE_GROUP_ID = "delivery-service-group";

    @Bean
    NewTopic orderPaidTopic() {
        return TopicBuilder.name(ORDER_PAID_TOPIC).partitions(1).replicas(1).build();
    }

    @Bean
    NewTopic deliveryCreatedTopic() {
        return TopicBuilder.name(DELIVERY_CREATED_TOPIC).partitions(1).replicas(1).build();
    }
}