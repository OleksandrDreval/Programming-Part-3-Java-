package ua.nure.ice.bookcatalog.laboratorna4.saga.model;

import jakarta.persistence.*;
import java.time.Instant;


@Entity
@Table(name = "idempotency_keys")
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Instant createdAt;

    public IdempotencyKey() {}

    public IdempotencyKey(String idempotencyKey, Long orderId) {
        this.idempotencyKey = idempotencyKey;
        this.orderId = orderId;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
