package ua.nure.ice.bookcatalog.laboratorna4.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.nure.ice.bookcatalog.laboratorna4.model.order.BookOrder;
import ua.nure.ice.bookcatalog.laboratorna4.repository.OrderRepository;
import ua.nure.ice.bookcatalog.laboratorna4.saga.dto.CreateSagaOrderRequest;
import ua.nure.ice.bookcatalog.laboratorna4.saga.service.OrderApplicationService;

import java.util.List;


@RestController
@RequestMapping("/api/saga/orders")
public class SagaOrderController {

    private final OrderApplicationService orderApplicationService;
    private final OrderRepository orderRepository;

    public SagaOrderController(OrderApplicationService orderApplicationService,
                               OrderRepository orderRepository) {
        this.orderApplicationService = orderApplicationService;
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CreateSagaOrderRequest request) {
        try {
            BookOrder order = orderApplicationService.createOrderWithIdempotency(
                    idempotencyKey, request.getCustomerName(), request.getBookIds());
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<BookOrder>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookOrder> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
