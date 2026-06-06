package ua.nure.ice.bookcatalog.laboratorna4.payment.saga.dto;

public class EventMessage {

    private String eventId;
    private String eventType;
    private String correlationId;
    private String payload;

    public EventMessage() {}

    public EventMessage(String eventId, String eventType, String correlationId, String payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.correlationId = correlationId;
        this.payload = payload;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
