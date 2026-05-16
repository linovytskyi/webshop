package com.example.paymentservice.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";
    public static final String ORDER_DLX = "order.dlx";
    public static final String PAYMENT_DLX = "payment.dlx";

    public static final String PAYMENT_REQUESTS_QUEUE = "payment.requests";
    public static final String PAYMENT_REQUESTS_DLQ = "payment.requests.dlq";
    public static final String ORDER_PAYMENT_RESULTS_QUEUE = "order.payment.results";
    public static final String ORDER_PAYMENT_RESULTS_DLQ = "order.payment.results.dlq";

    public static final String ROUTING_KEY_PAYMENT_REQUEST = "order.payment.request";
    public static final String ROUTING_KEY_PAYMENT_WILDCARD = "payment.#";

    @Bean
    TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    TopicExchange paymentExchange() {
        return new TopicExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    DirectExchange orderDlx() {
        return new DirectExchange(ORDER_DLX);
    }

    @Bean
    DirectExchange paymentDlx() {
        return new DirectExchange(PAYMENT_DLX);
    }

    @Bean
    Queue paymentRequestsQueue() {
        return QueueBuilder.durable(PAYMENT_REQUESTS_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_DLX)
                .withArgument("x-dead-letter-routing-key", PAYMENT_REQUESTS_DLQ)
                .build();
    }

    @Bean
    Queue paymentRequestsDlq() {
        return QueueBuilder.durable(PAYMENT_REQUESTS_DLQ).build();
    }

    @Bean
    Queue orderPaymentResultsQueue() {
        return QueueBuilder.durable(ORDER_PAYMENT_RESULTS_QUEUE)
                .withArgument("x-dead-letter-exchange", PAYMENT_DLX)
                .withArgument("x-dead-letter-routing-key", ORDER_PAYMENT_RESULTS_DLQ)
                .build();
    }

    @Bean
    Queue orderPaymentResultsDlq() {
        return QueueBuilder.durable(ORDER_PAYMENT_RESULTS_DLQ).build();
    }

    @Bean
    Binding paymentRequestsBinding() {
        return BindingBuilder.bind(paymentRequestsQueue())
                .to(orderExchange())
                .with(ROUTING_KEY_PAYMENT_REQUEST);
    }

    @Bean
    Binding orderPaymentResultsBinding() {
        return BindingBuilder.bind(orderPaymentResultsQueue())
                .to(paymentExchange())
                .with(ROUTING_KEY_PAYMENT_WILDCARD);
    }

    @Bean
    Binding paymentRequestsDlqBinding() {
        return BindingBuilder.bind(paymentRequestsDlq())
                .to(orderDlx())
                .with(PAYMENT_REQUESTS_DLQ);
    }

    @Bean
    Binding orderPaymentResultsDlqBinding() {
        return BindingBuilder.bind(orderPaymentResultsDlq())
                .to(paymentDlx())
                .with(ORDER_PAYMENT_RESULTS_DLQ);
    }

    @Bean
    Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}