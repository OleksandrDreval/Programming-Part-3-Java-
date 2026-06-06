package ua.nure.ice.bookcatalog.laboratorna4.saga.dto;


public class PaymentResultPayload {

    private Long orderId;
    private Long paymentId;

    public PaymentResultPayload() {}

    public PaymentResultPayload(Long orderId, Long paymentId) {
        this.orderId = orderId;
        this.paymentId = paymentId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }
}
