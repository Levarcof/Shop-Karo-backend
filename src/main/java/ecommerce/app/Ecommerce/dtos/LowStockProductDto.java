package ecommerce.app.Ecommerce.dtos;

public record LowStockProductDto(
        Long productId,

        String productName,

        String sku,

        Integer stockQuantity,

        String imageUrl
) {
}
