package ecommerce.app.Ecommerce.dtos;

import java.math.BigDecimal;
import java.util.List;

public record DashboardOverviewDto(

        BigDecimal totalRevenue,

        Long totalOrders,

        Long totalCustomers,

        Long pendingOrders,

        Long totalProducts,

        List<LowStockProductDto> lowStockProducts,

        BigDecimal averageOrderValue
) {
}
