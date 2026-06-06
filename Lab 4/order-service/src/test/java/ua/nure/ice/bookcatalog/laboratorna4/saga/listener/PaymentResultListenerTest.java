package ua.nure.ice.bookcatalog.laboratorna4.saga.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.nure.ice.bookcatalog.laboratorna4.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.saga.service.OrderPaymentEventService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentResultListenerTest {

    @Mock
    private OrderPaymentEventService orderPaymentEventService;

    @InjectMocks
    private PaymentResultListener listener;

    @Test
    void handlePaymentReserved_shouldDelegateToService() {
        EventMessage message = new EventMessage("evt-001", "PaymentReserved", "saga-001", "{}");

        listener.handlePaymentReserved(message);

        verify(orderPaymentEventService).handlePaymentResult(message);
    }

    @Test
    void handlePaymentRejected_shouldDelegateToService() {
        EventMessage message = new EventMessage("evt-002", "PaymentRejected", "saga-002", "{}");

        listener.handlePaymentRejected(message);

        verify(orderPaymentEventService).handlePaymentResult(message);
    }
}
