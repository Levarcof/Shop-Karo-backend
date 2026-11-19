package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.entity.Payment;
import ecommerce.app.Ecommerce.entity.User;
import ecommerce.app.Ecommerce.enums.OrderStatus;
import ecommerce.app.Ecommerce.enums.PaymentMethod;
import ecommerce.app.Ecommerce.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdminOrderDetails {
    private Long orderId;
    private OrderStatus orderStatus;
    private BigDecimal subTotal;
    private List<OrderItemDto> products = new ArrayList<>();
    private UserDetailDto user;
    private List<PaymentDetailDto> payments = new ArrayList<>();



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

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public List<OrderItemDto> getProducts() {
        return products;
    }

    public void setProducts(List<OrderItemDto> products) {
        this.products = products;
    }

    public UserDetailDto getUser() {
        return user;
    }

    public void setUser(UserDetailDto user) {
        this.user = user;
    }

    public List<PaymentDetailDto> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentDetailDto> payments) {
        this.payments = payments;
    }
}
