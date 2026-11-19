package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.OrderItemStatus;

import java.math.BigDecimal;

public class OrderItemDto {
    private Long orderItemId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal totalAmount;
    private String imageUrl;
    private OrderItemStatus status;

    public OrderItemDto(Long orderItemId, Long productId, String productName, Integer quantity, BigDecimal totalAmount, String imageUrl , OrderItemStatus status) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.imageUrl = imageUrl;
        this.status = status;
    }


    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }
}
