package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.OrderStatus;

public class PaymentResult {
    private Long orderId;
    private OrderStatus orderStatus;

    public PaymentResult(Long orderId, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
