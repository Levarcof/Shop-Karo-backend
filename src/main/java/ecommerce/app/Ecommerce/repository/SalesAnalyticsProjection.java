package ecommerce.app.Ecommerce.repository;

import java.math.BigDecimal;

public interface SalesAnalyticsProjection {
    Integer getMonthNumber();

    String getMonthName();

    Long getTotalOrders();

    BigDecimal getTotalRevenue();
}
