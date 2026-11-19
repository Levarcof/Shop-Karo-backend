package ecommerce.app.Ecommerce.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import ecommerce.app.Ecommerce.enums.OrderStatus;
import ecommerce.app.Ecommerce.enums.PaymentMethod;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CheckoutResponse (
    Long orderId,
    OrderStatus orderStatus,
    PaymentMethod paymentMethod,
    String razorpayOrderId,    // sirf ONLINE
    Long amountPaise,          // sirf ONLINE
    String currency,           // sirf ONLINE
    String keyId,              // sirf ONLINE (public key, secret nahi)
    String message
)
{
    public static CheckoutResponse cod(Long orderId) {
        return new CheckoutResponse(orderId, OrderStatus.PLACED, PaymentMethod.COD,
                null, null, null, null, "Order placed successfully");
    }

    public static CheckoutResponse online(Long orderId, String razorpayOrderId,
                                          long amountPaise, String keyId) {
        return new CheckoutResponse(orderId, OrderStatus.PAYMENT_PENDING, PaymentMethod.ONLINE,
                razorpayOrderId, amountPaise, "INR", keyId, "Complete the payment");
    }

    public static CheckoutResponse status(Long orderId, OrderStatus status, PaymentMethod method) {
        return new CheckoutResponse(orderId, status, method,
                null, null, null, null, "Order already processed");
    }
}

