package ua.nure.ice.bookcatalog.laboratorna4.payment.model;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import org.junit.jupiter.api.Test;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.OutboxEvent;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.OutboxEventStatus;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.model.ProcessedEvent;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


class PaymentModelPojoTest {

    @Test
    void validatePaymentGetters() {
        PojoClass paymentClass = PojoClassFactory.getPojoClass(Payment.class);

        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new GetterTester())
                .build();

        validator.validate(paymentClass);
    }

    @Test
    void payment_constructor_shouldSetFieldsCorrectly() {
        Payment payment = new Payment(1L, new BigDecimal("500.00"), PaymentStatus.RESERVED);

        assertEquals(1L, payment.getOrderId());
        assertEquals(new BigDecimal("500.00"), payment.getAmount());
        assertEquals(PaymentStatus.RESERVED, payment.getStatus());
        assertNotNull(payment.getCreatedAt());
    }

    @Test
    void paymentStatus_shouldHaveExpectedValues() {
        assertEquals(2, PaymentStatus.values().length);
        assertNotNull(PaymentStatus.valueOf("RESERVED"));
        assertNotNull(PaymentStatus.valueOf("REJECTED"));
    }

    @Test
    void outboxEvent_constructor_shouldSetFieldsCorrectly() {
        OutboxEvent event = new OutboxEvent("evt-001", "PaymentReserved", "{}", "saga-001");

        assertEquals("evt-001", event.getEventId());
        assertEquals("PaymentReserved", event.getEventType());
        assertEquals("{}", event.getPayload());
        assertEquals("saga-001", event.getCorrelationId());
        assertEquals(OutboxEventStatus.NEW, event.getStatus());
        assertNotNull(event.getCreatedAt());
    }

    @Test
    void outboxEvent_markPublished_shouldChangeStatus() {
        OutboxEvent event = new OutboxEvent("evt-001", "PaymentReserved", "{}", "saga-001");

        event.markPublished();

        assertEquals(OutboxEventStatus.PUBLISHED, event.getStatus());
    }

    @Test
    void processedEvent_constructor_shouldSetFieldsCorrectly() {
        ProcessedEvent event = new ProcessedEvent("evt-001");

        assertEquals("evt-001", event.getEventId());
        assertNotNull(event.getProcessedAt());
    }

    @Test
    void outboxEventStatus_shouldHaveExpectedValues() {
        assertEquals(2, OutboxEventStatus.values().length);
        assertNotNull(OutboxEventStatus.valueOf("NEW"));
        assertNotNull(OutboxEventStatus.valueOf("PUBLISHED"));
    }

    @Test
    void outboxEvent_emptyConstructor() {
        OutboxEvent event = new OutboxEvent();
        assertNull(event.getEventId());
    }

    @Test
    void processedEvent_emptyConstructor() {
        ProcessedEvent event = new ProcessedEvent();
        assertNull(event.getEventId());
    }
}
