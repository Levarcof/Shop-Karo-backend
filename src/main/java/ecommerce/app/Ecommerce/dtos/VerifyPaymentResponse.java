package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.OrderStatus;

public class VerifyPaymentResponse {
    private Long orderId ;
    private OrderStatus orderStatus;
    private  String message;


    public VerifyPaymentResponse(Long orderId, OrderStatus orderStatus, String message) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
