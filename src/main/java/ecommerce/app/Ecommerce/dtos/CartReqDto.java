package ecommerce.app.Ecommerce.dtos;

import java.util.ArrayList;
import java.util.List;

public class CartReqDto {
    private Long productId;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
