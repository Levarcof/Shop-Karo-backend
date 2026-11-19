package ecommerce.app.Ecommerce.repository;

import java.math.BigDecimal;

public interface MonthlySalesProjection {
    Integer getMonth();
    Long getTotalOrders();
    BigDecimal getTotalSales();

}
