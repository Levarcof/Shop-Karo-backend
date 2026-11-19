package ecommerce.app.Ecommerce.dtos;

import java.math.BigDecimal;

public record MonthlySalesDto(
        Integer month,
        String monthName,
        Long totalOrders,
        BigDecimal totalSales
) {
}
