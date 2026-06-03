package ua.nure.ice.bookcatalog.laboratorna3.model.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ua.nure.ice.bookcatalog.laboratorna3.model.Book;

import jakarta.persistence.*;

@Entity
@Table(name = "book_orders")
public class BookOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String customerName;
    
    @ManyToMany
    @JoinTable(
        name = "book_order_books",
        joinColumns = @JoinColumn(name = "order_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private List<Book> books;
    
    @Embedded
    private DeliveryAddress deliveryAddress;
    
    @Column(length = 500)
    private String orderComment;
    
    @Column(nullable = false)
    private boolean isUrgent;
    
    @Column(length = 100)
    private String paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    public BookOrder() {}

    private BookOrder(Builder builder) {
        this.id = builder.id;
        this.customerName = builder.customerName;
        this.books = Collections.unmodifiableList(new ArrayList<>(builder.books));
        this.deliveryAddress = builder.deliveryAddress;
        this.orderComment = builder.orderComment;
        this.isUrgent = builder.isUrgent;
        this.paymentMethod = builder.paymentMethod;
        this.status = builder.status;
    }

    public Long getId() {
        return id;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Order #").append(id).append("\n");
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
        private Long id;
        private final String customerName;

        // Optional fields
        private List<Book> books = new ArrayList<>();
        private DeliveryAddress deliveryAddress;
        private String orderComment;
        private boolean isUrgent = false;
        private String paymentMethod;
        private OrderStatus status = OrderStatus.NEW;

        public Builder(Long id, String customerName) {
            if (customerName == null || customerName.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer name cannot be null or empty");
            }

            this.id = id;
            this.customerName = customerName;
        }

        public Builder(String customerName) {
            this(null, customerName);
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

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public BookOrder build() {
            return new BookOrder(this);
        }
    }
}
