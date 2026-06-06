package ua.nure.ice.bookcatalog.laboratorna4.saga.model;

import jakarta.persistence.*;
import java.time.Instant;


@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String eventId;

    @Column(nullable = false, length = 50)
    private String eventType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false, length = 36)
    private String correlationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxEventStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    public OutboxEvent() {}

    public OutboxEvent(String eventId, String eventType, String payload, String correlationId) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
        this.correlationId = correlationId;
        this.status = OutboxEventStatus.NEW;
        this.createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public OutboxEventStatus getStatus() {
        return status;
    }

    public void markPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
