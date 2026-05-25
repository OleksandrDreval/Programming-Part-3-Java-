package ua.nure.ice.bookcatalog.laboratorna1.service;

import org.springframework.stereotype.Service;
import ua.nure.ice.bookcatalog.laboratorna1.model.Book;
import ua.nure.ice.bookcatalog.laboratorna1.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna1.model.order.OrderStatus;
import ua.nure.ice.bookcatalog.laboratorna1.repository.BookRepository;
import ua.nure.ice.bookcatalog.laboratorna1.repository.OrderRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;

    public OrderService(OrderRepository orderRepository, BookRepository bookRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
    }

    public List<BookOrder> findAllOrders() {
        return orderRepository.findAll();
    }

    public BookOrder findOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    public BookOrder createOrder(BookOrder orderRequest) {
        if (orderRequest.getBooks() == null || orderRequest.getBooks().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one book.");
        }

        // Verify that all requested books actually exist
        for (Book b : orderRequest.getBooks()) {
            bookRepository.findById(b.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Book not found: " + b.getId()));
        }

        // Conflict check: customer cannot order the exact same book if they already have an active order for it.
        List<BookOrder> activeCustomerOrders = orderRepository.findAll().stream()
                .filter(o -> o.getCustomerName().equals(orderRequest.getCustomerName()))
                .filter(o -> o.getStatus() == OrderStatus.NEW || o.getStatus() == OrderStatus.APPROVED)
                .collect(Collectors.toList());

        for (Book requestedBook : orderRequest.getBooks()) {
            boolean hasConflict = activeCustomerOrders.stream()
                    .flatMap(o -> o.getBooks().stream())
                    .anyMatch(b -> b.getId() == requestedBook.getId());

            if (hasConflict) {
                throw new IllegalStateException("Conflict: You already have an active order for book " + requestedBook.getTitle());
            }
        }

        String orderId = (orderRequest.getOrderId() == null || orderRequest.getOrderId().isBlank()) 
                ? UUID.randomUUID().toString() 
                : orderRequest.getOrderId();

        BookOrder newOrder = new BookOrder.Builder(orderId, orderRequest.getCustomerName())
                .books(orderRequest.getBooks())
                .deliveryAddress(orderRequest.getDeliveryAddress())
                .orderComment(orderRequest.getOrderComment())
                .isUrgent(orderRequest.isUrgent())
                .paymentMethod(orderRequest.getPaymentMethod())
                .status(OrderStatus.NEW)
                .build();

        return orderRepository.save(newOrder);
    }

    public BookOrder updateOrderStatus(String id, OrderStatus newStatus) {
        BookOrder existing = findOrderById(id);

        if (existing.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a cancelled order");
        }

        BookOrder updatedOrder = new BookOrder.Builder(existing.getOrderId(), existing.getCustomerName())
                .books(existing.getBooks())
                .deliveryAddress(existing.getDeliveryAddress())
                .orderComment(existing.getOrderComment())
                .isUrgent(existing.isUrgent())
                .paymentMethod(existing.getPaymentMethod())
                .status(newStatus)
                .build();

        return orderRepository.save(updatedOrder);
    }

    public void deleteOrder(String id) {
        if (!orderRepository.deleteById(id)) {
            throw new IllegalArgumentException("Order not found: " + id);
        }
    }
}
