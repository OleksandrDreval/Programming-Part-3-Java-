package ua.nure.ice.bookcatalog.laboratorna4.saga.model;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class SagaModelPojoTest {

    @Test
    void validateOutboxEventGetters() {
        PojoClass outboxEventClass = PojoClassFactory.getPojoClass(OutboxEvent.class);

        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new GetterTester())
                .build();

        validator.validate(outboxEventClass);
    }

    @Test
    void validateProcessedEventGetters() {
        PojoClass processedEventClass = PojoClassFactory.getPojoClass(ProcessedEvent.class);

        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new GetterTester())
                .build();

        validator.validate(processedEventClass);
    }

    @Test
    void validateIdempotencyKeyGetters() {
        PojoClass idempotencyKeyClass = PojoClassFactory.getPojoClass(IdempotencyKey.class);

        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new GetterTester())
                .build();

        validator.validate(idempotencyKeyClass);
    }

    @Test
    void outboxEvent_constructor_shouldSetFieldsCorrectly() {
        OutboxEvent event = new OutboxEvent("evt-001", "OrderCreated", "{}", "saga-001");

        assertEquals("evt-001", event.getEventId());
        assertEquals("OrderCreated", event.getEventType());
        assertEquals("{}", event.getPayload());
        assertEquals("saga-001", event.getCorrelationId());
        assertEquals(OutboxEventStatus.NEW, event.getStatus());
        assertNotNull(event.getCreatedAt());
    }

    @Test
    void outboxEvent_markPublished_shouldChangeStatus() {
        OutboxEvent event = new OutboxEvent("evt-001", "OrderCreated", "{}", "saga-001");

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
    void idempotencyKey_constructor_shouldSetFieldsCorrectly() {
        IdempotencyKey key = new IdempotencyKey("key-001", 42L);

        assertEquals("key-001", key.getIdempotencyKey());
        assertEquals(42L, key.getOrderId());
        assertNotNull(key.getCreatedAt());
    }

    @Test
    void outboxEventStatus_shouldHaveExpectedValues() {
        assertEquals(2, OutboxEventStatus.values().length);
        assertNotNull(OutboxEventStatus.valueOf("NEW"));
        assertNotNull(OutboxEventStatus.valueOf("PUBLISHED"));
    }
}
