package ua.nure.ice.bookcatalog.laboratorna4.payment.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.config.RabbitMqConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = PaymentApp.class)
class PaymentAppTest {

    @MockitoBean
    private ConnectionFactory connectionFactory;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private SimpleMessageListenerContainer simpleMessageListenerContainer;

    @Test
    void contextLoads() {
        
        RabbitMqConfig config = new RabbitMqConfig();
        assertNotNull(config.sagaExchange());
        assertNotNull(config.orderCreatedQueue());
        assertNotNull(config.orderCreatedBinding(config.orderCreatedQueue(), config.sagaExchange()));
        assertNotNull(config.jsonMessageConverter());
    }
}
