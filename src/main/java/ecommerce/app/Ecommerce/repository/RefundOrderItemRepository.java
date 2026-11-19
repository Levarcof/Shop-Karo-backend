package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.RefundOrderItem;
import ecommerce.app.Ecommerce.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface RefundOrderItemRepository extends JpaRepository<RefundOrderItem , Long> {
    @Modifying
    @Query("""
    UPDATE RefundOrderItem r
    SET r.status = :processing
    WHERE r.id = :id
    AND r.status = :pending
""")
    int claimRefund(
            @Param("id") Long id,
            @Param("pending") RefundStatus pending,
            @Param("processing") RefundStatus processing
    );
}
