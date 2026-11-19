package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem , Long> {
    @Query("SELECT i FROM OrderItem i WHERE i.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    List<OrderItem> findByOrderIdAndIdIn(
            Long orderId,
            List<Long> orderItemIds
    );

}
