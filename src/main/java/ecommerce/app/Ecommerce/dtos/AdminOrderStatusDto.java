package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.OrderStatus;

public class AdminOrderStatusDto {
    private Long id;
    private OrderStatus orderStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
