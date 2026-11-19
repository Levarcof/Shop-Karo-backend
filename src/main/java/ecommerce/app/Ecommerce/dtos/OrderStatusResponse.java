package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.OrderStatus;

public class OrderStatusResponse {
    private Long orderId;
    private OrderStatus status;

    public OrderStatusResponse(Long orderId, OrderStatus status) {
        this.orderId = orderId;
        this.status = status;
    }
}
