package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.entity.OrderItem;
import ecommerce.app.Ecommerce.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class CheckoutRequest {
    @NotNull( message = "AdressId required")
    private Long addressId;

    @NotNull(message = "Payment method required")
    private PaymentMethod paymentMethod;

    @NotEmpty(message = "Cart must not be empty")
    @Size(max = 50 , message = "Max 50 items allowed")
    @Valid
    private List<OrderItemReq> items = new ArrayList<>();






    public @NotNull(message = "AdressId required") Long getAddressId() {
        return addressId;
    }

    public void setAddressId(@NotNull(message = "AdressId required") Long addressId) {
        this.addressId = addressId;
    }

    public @NotNull(message = "Payment method required") PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(@NotNull(message = "Payment method required") PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public @NotEmpty(message = "Cart must not be empty") @Size(max = 50, message = "Max 50 items allowed") @Valid List<OrderItemReq> getItems() {
        return items;
    }

    public void setItems(@NotEmpty(message = "Cart must not be empty") @Size(max = 50, message = "Max 50 items allowed") @Valid List<OrderItemReq> items) {
        this.items = items;
    }
}
