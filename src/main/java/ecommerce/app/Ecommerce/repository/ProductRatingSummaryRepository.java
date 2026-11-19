package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.ProductRatingSummary;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRatingSummaryRepository  extends JpaRepository<ProductRatingSummary , Long> {

    Optional<ProductRatingSummary> findByProductId(Long productId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT s
        FROM ProductRatingSummary s
        WHERE s.product.id = :productId
    """)
    Optional<ProductRatingSummary> findByProductIdForUpdate(
            @Param("productId") Long productId
    );
}
