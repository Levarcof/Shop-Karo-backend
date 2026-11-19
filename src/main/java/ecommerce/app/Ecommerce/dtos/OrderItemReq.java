package ecommerce.app.Ecommerce.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class OrderItemReq {
    @NotNull(message = "ProductId is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1 , message = "Quantity must be atleast 1")
    private Integer quantity;







    public @NotNull(message = "ProductId is required") Long getProductId() {
        return productId;
    }

    public void setProductId(@NotNull(message = "ProductId is required") Long productId) {
        this.productId = productId;
    }

    public @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be atleast 1") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be atleast 1") Integer quantity) {
        this.quantity = quantity;
    }
}
