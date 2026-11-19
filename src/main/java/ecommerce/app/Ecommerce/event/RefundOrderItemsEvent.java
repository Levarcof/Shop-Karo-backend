package ecommerce.app.Ecommerce.event;

public record RefundOrderItemsEvent(
        Long refundId,
        long amountPaise,
        String razorpayPaymentId
) {
}
