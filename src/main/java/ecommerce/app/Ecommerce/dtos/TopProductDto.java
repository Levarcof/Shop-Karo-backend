package ecommerce.app.Ecommerce.dtos;

import java.math.BigDecimal;

public record TopProductDto(
        Long productId,

        String productName,

        String sku,

        Long totalQuantitySold,

        BigDecimal totalRevenue,

        String imageUrl
) {
}
