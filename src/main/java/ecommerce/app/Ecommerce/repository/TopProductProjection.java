package ecommerce.app.Ecommerce.repository;

import java.math.BigDecimal;

public interface TopProductProjection {

    Long getProductId();

    String getProductName();

    String getSku();

    Long getTotalQuantitySold();

    BigDecimal getTotalRevenue();

    String getImageUrl();
}
