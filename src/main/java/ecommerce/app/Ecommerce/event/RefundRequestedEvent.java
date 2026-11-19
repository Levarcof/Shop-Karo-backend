package ecommerce.app.Ecommerce.event;

public record RefundRequestedEvent(
        String razorpayPaymentId,
        long amountPaise
) {
}
