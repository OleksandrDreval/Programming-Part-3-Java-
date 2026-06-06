package ua.nure.ice.bookcatalog.laboratorna4.payment.saga.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String SAGA_EXCHANGE = "saga.exchange";

    public static final String ORDER_CREATED_QUEUE = "payment.order-created.queue";
    public static final String PAYMENT_RESERVED_QUEUE = "order.payment-reserved.queue";
    public static final String PAYMENT_REJECTED_QUEUE = "order.payment-rejected.queue";

    public static final String ORDER_CREATED_ROUTING_KEY = "OrderCreated";
    public static final String PAYMENT_RESERVED_ROUTING_KEY = "PaymentReserved";
    public static final String PAYMENT_REJECTED_ROUTING_KEY = "PaymentRejected";

    @Bean
    public DirectExchange sagaExchange() {
        return new DirectExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE).build();
    }

    @Bean
    public Queue paymentReservedQueue() {
        return QueueBuilder.durable(PAYMENT_RESERVED_QUEUE).build();
    }

    @Bean
    public Queue paymentRejectedQueue() {
        return QueueBuilder.durable(PAYMENT_REJECTED_QUEUE).build();
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(sagaExchange).with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentReservedBinding(Queue paymentReservedQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(paymentReservedQueue).to(sagaExchange).with(PAYMENT_RESERVED_ROUTING_KEY);
    }

    @Bean
    public Binding paymentRejectedBinding(Queue paymentRejectedQueue, DirectExchange sagaExchange) {
        return BindingBuilder.bind(paymentRejectedQueue).to(sagaExchange).with(PAYMENT_REJECTED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        org.springframework.amqp.support.converter.DefaultClassMapper classMapper = new org.springframework.amqp.support.converter.DefaultClassMapper();
        java.util.Map<String, Class<?>> idClassMapping = new java.util.HashMap<>();
        idClassMapping.put("ua.nure.ice.bookcatalog.laboratorna4.saga.dto.EventMessage", 
            ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto.EventMessage.class);
        classMapper.setIdClassMapping(idClassMapping);
        classMapper.setTrustedPackages("*");
        converter.setClassMapper(classMapper);
        return converter;
    }
}
