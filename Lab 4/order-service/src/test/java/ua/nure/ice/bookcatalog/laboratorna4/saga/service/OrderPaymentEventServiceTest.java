package ua.nure.ice.bookcatalog.laboratorna4.saga.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.nure.ice.bookcatalog.laboratorna4.model.Book;
import ua.nure.ice.bookcatalog.laboratorna4.model.BookGenre;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.OrderStatus;
import ua.nure.ice.bookcatalog.laboratorna4.repository.OrderRepository;
import ua.nure.ice.bookcatalog.laboratorna4.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.saga.dto.PaymentResultPayload;
import ua.nure.ice.bookcatalog.laboratorna4.saga.model.ProcessedEvent;
import ua.nure.ice.bookcatalog.laboratorna4.saga.repository.ProcessedEventRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPaymentEventServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private OrderPaymentEventService service;

    private BookOrder pendingOrder;
    private EventMessage reservedMessage;
    private EventMessage rejectedMessage;

    @BeforeEach
    void setUp() throws Exception {
        Book book = new Book(1L, "Book", "Author", 2023, BookGenre.FICTION, new BigDecimal("100.00"));
        pendingOrder = new BookOrder.Builder(1L, "Alice")
                .addBook(book)
                .status(OrderStatus.PENDING_PAYMENT)
                .build();

        PaymentResultPayload payload = new PaymentResultPayload(1L, 10L);
        String payloadJson = objectMapper.writeValueAsString(payload);

        reservedMessage = new EventMessage("event-001", "PaymentReserved", "saga-001", payloadJson);
        rejectedMessage = new EventMessage("event-002", "PaymentRejected", "saga-002", payloadJson);
    }

    @Test
    void handlePaymentResult_paymentReserved_shouldConfirmOrder() {
        when(processedEventRepository.existsByEventId("event-001")).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));

        service.handlePaymentResult(reservedMessage);

        assertEquals(OrderStatus.CONFIRMED, pendingOrder.getStatus());
        verify(orderRepository).save(pendingOrder);
        verify(processedEventRepository).save(any(ProcessedEvent.class));
    }

    @Test
    void handlePaymentResult_paymentRejected_shouldCancelOrder() {
        when(processedEventRepository.existsByEventId("event-002")).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));

        service.handlePaymentResult(rejectedMessage);

        assertEquals(OrderStatus.CANCELLED, pendingOrder.getStatus());
        verify(orderRepository).save(pendingOrder);
        verify(processedEventRepository).save(any(ProcessedEvent.class));
    }

    @Test
    void handlePaymentResult_duplicateEvent_shouldSkipProcessing() {
        when(processedEventRepository.existsByEventId("event-001")).thenReturn(true);

        service.handlePaymentResult(reservedMessage);

        verify(orderRepository, never()).findById(any());
        verify(orderRepository, never()).save(any());
        verify(processedEventRepository, never()).save(any());
    }

    @Test
    void handlePaymentResult_orderNotFound_shouldThrowException() {
        when(processedEventRepository.existsByEventId("event-001")).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> service.handlePaymentResult(reservedMessage));
    }

    @Test
    void handlePaymentResult_unknownEventType_shouldNotModifyOrder() {
        EventMessage unknownMessage = new EventMessage("event-003", "UnknownEvent", "saga-003",
                reservedMessage.getPayload());

        when(processedEventRepository.existsByEventId("event-003")).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));

        service.handlePaymentResult(unknownMessage);

        assertEquals(OrderStatus.PENDING_PAYMENT, pendingOrder.getStatus());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void handlePaymentResult_shouldSaveCorrectEventId() {
        when(processedEventRepository.existsByEventId("event-001")).thenReturn(false);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));

        service.handlePaymentResult(reservedMessage);

        ArgumentCaptor<ProcessedEvent> captor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventRepository).save(captor.capture());
        assertEquals("event-001", captor.getValue().getEventId());
    }

    @Test
    void handlePaymentResult_invalidPayloadJson_shouldThrowException() {
        EventMessage badMessage = new EventMessage("event-bad", "PaymentReserved", "saga-bad", "not-json");
        when(processedEventRepository.existsByEventId("event-bad")).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> service.handlePaymentResult(badMessage));
    }
}
