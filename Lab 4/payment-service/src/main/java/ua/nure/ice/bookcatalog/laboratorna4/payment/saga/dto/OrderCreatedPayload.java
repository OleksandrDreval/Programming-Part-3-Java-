package ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto;

import java.math.BigDecimal;

public class OrderCreatedPayload {

    private Long orderId;
    private String customerName;
    private BigDecimal amount;

    public OrderCreatedPayload() {}

    public OrderCreatedPayload(Long orderId, String customerName, BigDecimal amount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.amount = amount;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
