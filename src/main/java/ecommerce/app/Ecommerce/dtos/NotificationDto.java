package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.NotificationType;
import ecommerce.app.Ecommerce.enums.RecipientType;
import ecommerce.app.Ecommerce.enums.ReferenceType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDateTime;

public class NotificationDto {
    private Long id;

    private NotificationType type;

    private Long recipientId;

    private RecipientType recipientType;

    private ReferenceType referenceType;

    private Long referenceId;

    private String title;

    private String message;

    private boolean isRead;

    private LocalDateTime readAt;

    private LocalDateTime createdAt;

    private boolean isResolved;

    private LocalDateTime resolvedAt;


    public NotificationDto(Long id ,NotificationType type, Long recipientId, RecipientType recipientType, ReferenceType referenceType, Long referenceId, String title, String message, boolean isRead, LocalDateTime readAt, LocalDateTime createdAt, boolean isResolved, LocalDateTime resolvedAt) {
        this.type = type;
        this.recipientId = recipientId;
        this.recipientType = recipientType;
        this.referenceType = referenceType;
        this.referenceId = referenceId;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.readAt = readAt;
        this.createdAt = createdAt;
        this.isResolved = isResolved;
        this.resolvedAt = resolvedAt;
        this.id = id;
    }







    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public RecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(RecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        isResolved = resolved;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
