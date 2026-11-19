package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.dtos.ProductSuggestionDto;
import ecommerce.app.Ecommerce.entity.Product;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product , Long> {
    boolean existsBySku(String sku);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findBySku(String sku);

    List<Product> findByNameContainingIgnoreCase(String trim);

    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :qty WHERE p.id = :id")
    int releaseStock(@Param("id") Long id, @Param("qty") int qty);

    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :qty WHERE p.id = :id AND p.stockQuantity >= :qty")
    int reserveStock(@Param("id") Long id, @Param("qty") int qty);

    List<ProductSuggestionDto> findTop10ByNameContainingIgnoreCase(String trim);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    @Query("""
        SELECT COUNT(p)
        FROM Product p
    """)
    Long getTotalProducts();
}
