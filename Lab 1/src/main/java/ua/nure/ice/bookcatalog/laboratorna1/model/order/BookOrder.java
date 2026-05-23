package ua.nure.ice.bookcatalog.laboratorna1.model.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ua.nure.ice.bookcatalog.laboratorna1.model.Book;

public class BookOrder {
    private final String orderId;
    private final String customerName;
    private final List<Book> books;
    private final DeliveryAddress deliveryAddress;
    private final String orderComment;
    private final boolean isUrgent;
    private final String paymentMethod;

    private BookOrder(Builder builder) {
        this.orderId = builder.orderId;
        this.customerName = builder.customerName;
        this.books = Collections.unmodifiableList(new ArrayList<>(builder.books));
        this.deliveryAddress = builder.deliveryAddress;
        this.orderComment = builder.orderComment;
        this.isUrgent = builder.isUrgent;
        this.paymentMethod = builder.paymentMethod;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<Book> getBooks() {
        return books;
    }

    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public String getOrderComment() {
        return orderComment;
    }

    public boolean isUrgent() {
        return isUrgent;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(orderId).append("\n");
        sb.append("Customer: ").append(customerName).append("\n");
        sb.append("Urgent: ").append(isUrgent ? "Yes" : "No").append("\n");
        sb.append("Payment Method: ").append(paymentMethod != null ? paymentMethod : "Not specified").append("\n");
        if (orderComment != null && !orderComment.isEmpty()) {
            sb.append("Comment: ").append(orderComment).append("\n");
        }
        sb.append("Delivery Address:\n");
        if (deliveryAddress != null) {
            sb.append(deliveryAddress.toString().indent(4));
        } else {
            sb.append("    Not specified\n");
        }
        sb.append("Books:\n");
        if (books.isEmpty()) {
            sb.append("    No books in order\n");
        } else {
            for (Book book : books) {
                sb.append("    - ").append(book.getTitle()).append(" by ").append(book.getAuthor()).append("\n");
            }
        }
        return sb.toString();
    }

    public static class Builder {
        // Required fields
        private final String orderId;
        private final String customerName;

        // Optional fields
        private List<Book> books = new ArrayList<>();
        private DeliveryAddress deliveryAddress;
        private String orderComment;
        private boolean isUrgent = false;
        private String paymentMethod;

        public Builder(String orderId, String customerName) {
            if (orderId == null || orderId.trim().isEmpty()) {
                throw new IllegalArgumentException("Order ID cannot be null or empty");
            }
            if (customerName == null || customerName.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer name cannot be null or empty");
            }

            this.orderId = orderId;
            this.customerName = customerName;
        }

        public Builder addBook(Book book) {
            if (book != null) {
                this.books.add(book);
            }
            return this;
        }
        
        public Builder books(List<Book> books) {
            if (books != null) {
                this.books = new ArrayList<>(books);
            }
            return this;
        }

        public Builder deliveryAddress(DeliveryAddress deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public Builder orderComment(String orderComment) {
            this.orderComment = orderComment;
            return this;
        }

        public Builder isUrgent(boolean isUrgent) {
            this.isUrgent = isUrgent;
            return this;
        }

        public Builder paymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public BookOrder build() {
            return new BookOrder(this);
        }
    }
}
