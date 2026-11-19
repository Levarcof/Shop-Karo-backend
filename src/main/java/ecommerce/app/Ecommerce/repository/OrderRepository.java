package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.dtos.OrderSummaryResponse;
import ecommerce.app.Ecommerce.entity.Order;
import ecommerce.app.Ecommerce.enums.OrderStatus;
import ecommerce.app.Ecommerce.enums.PaymentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable; // ✅ Sahi
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order , Long> {
    Optional<Order> findByUserIdAndIdempotencyKey(Long userId, String idemKey);

    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :to WHERE o.id = :id AND o.status = :from")
    int transition(@Param("id") Long id, @Param("from") OrderStatus from, @Param("to") OrderStatus to);

    Optional<Order> findById(Long id);

    boolean existsByIdAndUserId(Long orderId, Long userId);

    @Query("SELECT o.status FROM Order o WHERE o.id = :id")
    OrderStatus findStatusById(@Param("id") Long id);

    @Query("SELECT o.id FROM Order o WHERE o.status = :status AND o.expiresAt < :now")
    List<Long> findExpiredIds(@Param("status") OrderStatus status,
                              @Param("now") LocalDateTime now, Pageable pageable);


    @Query("SELECT o.status FROM Order o WHERE o.id = :id AND o.user.id = :userId")
    Optional<OrderStatus> findStatusByIdAndUser(@Param("id") Long id, @Param("userId") Long userId);



    Page<Order> findByUser_IdAndStatusIn(
            Long userId,
            List<OrderStatus> statuses,
            Pageable pageable
    );


    @EntityGraph(attributePaths = {"user"})
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @EntityGraph(attributePaths = {
            "user",
            "items",
            "items.product"
    })
    @Query("SELECT o FROM Order o WHERE o.id = :id")
    Optional<Order> findDetailsById(@Param("id") Long id);

    Optional<Order> findByIdAndUserId(Long orderId, Long userId);


    // OrderRepository
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id and o.user.id = :userId")
    Optional<Order> findByIdAndUserIdForUpdate(@Param("id") Long id, @Param("userId") Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);
}
