package ecommerce.app.Ecommerce.enums;

public enum NotificationType {
    // Order notifications
    ORDER_PLACED,
    ORDER_CONFIRMED,
    ORDER_SHIPPED,
    ORDER_DELIVERED,
    ORDER_CANCELLED,
    ORDER_PROCESSING_DELAYED,

    REVIEW_CREATED,

    // Payment notifications
    PAYMENT_SUCCESS,
    PAYMENT_FAILED,
    PAYMENT_PENDING,

    // Return and refund notifications
    RETURN_REQUESTED,
    RETURN_APPROVED,
    RETURN_REJECTED,
    REFUND_INITIATED,
    REFUND_SUCCESS,
    REFUND_FAILED,

    // Account notifications
    ACCOUNT_CREATED,
    EMAIL_VERIFIED,
    PASSWORD_CHANGED,
    NEW_DEVICE_LOGIN,

    // Inventory notifications
    PRODUCT_ADD,
    CATEGORY_ADD,
    DELETED_PRODUCT,
    DELETED_CATEGORY,
    UPDATE_PRODUCT,
    UPDATE_CATEGORY,
    LOW_STOCK,
    OUT_OF_STOCK,
    INVENTORY_DISCREPANCY,

    // Support notifications
    SUPPORT_TICKET_CREATED,
    SUPPORT_TICKET_ESCALATED,

    // System notifications
    SYSTEM_ERROR,
    REVIEW_UPDATED, ORDER_PARTIALLY_CANCELLED, WEBHOOK_FAILURE
}
