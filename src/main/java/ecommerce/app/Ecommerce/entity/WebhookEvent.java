package ecommerce.app.Ecommerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class WebhookEvent {
    @Id
    private String eventId;          // X-Razorpay-Event-Id header

    private String eventType;
    private LocalDateTime receivedAt;

    public WebhookEvent() {
    }

    public WebhookEvent(String eventId, String eventType, LocalDateTime receivedAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.receivedAt = receivedAt;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
