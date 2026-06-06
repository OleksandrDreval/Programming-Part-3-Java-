package ua.nure.ice.bookcatalog.laboratorna4.saga.dto;

import java.util.List;


public class CreateSagaOrderRequest {

    private String customerName;
    private List<Long> bookIds;

    public CreateSagaOrderRequest() {}

    public CreateSagaOrderRequest(String customerName, List<Long> bookIds) {
        this.customerName = customerName;
        this.bookIds = bookIds;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<Long> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<Long> bookIds) {
        this.bookIds = bookIds;
    }
}
