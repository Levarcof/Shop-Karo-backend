package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderSummaryResponse {

    private Long orderId;
   private OrderStatus status;
   private BigDecimal totalAmount;
   private LocalDateTime createdAt;
   private List<OrderItemDto> products = new ArrayList<>();


    public OrderSummaryResponse(Long orderId, OrderStatus status, BigDecimal totalAmount, LocalDateTime createdAt , List<OrderItemDto> products) {
        this.orderId = orderId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.products = products;
    }

    public List<OrderItemDto> getProducts() {
        return products;
    }

    public void setProducts(List<OrderItemDto> products) {
        this.products = products;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
