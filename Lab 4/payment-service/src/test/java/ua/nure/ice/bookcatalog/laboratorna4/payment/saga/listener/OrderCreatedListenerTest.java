package ua.nure.ice.bookcatalog.laboratorna4.payment.saga.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto.EventMessage;
import ua.nure.ice.bookcatalog.laboratorna4.payment.saga.service.PaymentApplicationService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderCreatedListenerTest {

    @Mock
    private PaymentApplicationService paymentApplicationService;

    @InjectMocks
    private OrderCreatedListener listener;

    @Test
    void handleOrderCreated_shouldDelegateToPaymentApplicationService() {
        EventMessage message = new EventMessage("evt-001", "OrderCreated", "saga-001", "{\"orderId\":1}");

        listener.handleOrderCreated(message);

        verify(paymentApplicationService).handleOrderCreated(message);
    }
}
