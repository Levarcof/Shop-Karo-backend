package ecommerce.app.Ecommerce.dtos;

import java.math.BigDecimal;

public record SalesAnalyticsDto(
        String period,

        BigDecimal revenue,

        Long totalOrders
) {
}
