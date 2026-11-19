package ecommerce.app.Ecommerce.dtos;

import java.util.ArrayList;
import java.util.List;

public class CartDetaiilsDto {
    private List<ProductDetailsDto> cartItems = new ArrayList<>();

    public List<ProductDetailsDto> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<ProductDetailsDto> cartItems) {
        this.cartItems = cartItems;
    }
}
