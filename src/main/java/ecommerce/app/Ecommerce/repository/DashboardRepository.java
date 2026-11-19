package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.dtos.LowStockProductDto;
import ecommerce.app.Ecommerce.entity.Order;
import ecommerce.app.Ecommerce.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface DashboardRepository extends JpaRepository<Order, Long> {

    // Orders in range (only the given statuses)
    @Query("""
        select count(o) from Order o
        where o.createdAt >= :from and o.createdAt < :to
          and o.status in :statuses
        """)
    Long getTotalOrders(@Param("from") LocalDateTime from,
                        @Param("to") LocalDateTime to,
                        @Param("statuses") Collection<OrderStatus> statuses);

    // Revenue in range
    @Query("""
        select coalesce(sum(o.totalAmount), 0) from Order o
        where o.createdAt >= :from and o.createdAt < :to
          and o.status in :statuses
        """)
    BigDecimal getRevenue(@Param("from") LocalDateTime from,
                          @Param("to") LocalDateTime to,
                          @Param("statuses") Collection<OrderStatus> statuses);

    // Orders waiting for action (all time)
    @Query("select count(o) from Order o where o.status in :statuses")
    Long getPendingOrders(@Param("statuses") Collection<OrderStatus> statuses);

    // Customers
    @Query("select count(u) from User u")
    Long getTotalCustomers();

    // Products
    @Query("select count(p) from Product p where p.active = true")
    Long getTotalProducts();

    // Low stock
    @Query("""
    select new ecommerce.app.Ecommerce.dtos.LowStockProductDto(
        p.id, p.name, p.sku, p.stockQuantity, p.imageUrl)
    from Product p
    where p.active = true and p.stockQuantity <= :threshold
    order by p.stockQuantity asc, p.name asc
    """)
    List<LowStockProductDto> findLowStockProducts(@Param("threshold") Integer threshold,
                                                  Pageable pageable);

    // Monthly sales (12 months are filled in by the service)
    @Query("""
        select extract(month from o.createdAt) as month,
               count(o) as totalOrders,
               coalesce(sum(o.totalAmount), 0) as totalSales
        from Order o
        where extract(year from o.createdAt) = :year
          and o.status in :statuses
        group by extract(month from o.createdAt)
        order by extract(month from o.createdAt)
        """)
    List<MonthlySalesProjection> findMonthlySales(@Param("year") Integer year,
                                                  @Param("statuses") Collection<OrderStatus> statuses);

    // Top products by revenue
    @Query("""
        select p.id as productId,
               p.name as productName,
               p.sku as sku,
               sum(oi.quantity) as totalQuantitySold,
               sum(oi.quantity * oi.unitPrice) as totalRevenue,
               p.imageUrl as imageUrl
        from OrderItem oi
        join oi.order o
        join oi.product p
        where o.createdAt >= :from and o.createdAt < :to
          and o.status in :statuses
        group by p.id, p.name, p.sku, p.imageUrl
        order by sum(oi.quantity * oi.unitPrice) desc
        """)
    List<TopProductProjection> findTopProducts(@Param("from") LocalDateTime from,
                                               @Param("to") LocalDateTime to,
                                               @Param("statuses") Collection<OrderStatus> statuses,
                                               Pageable pageable);



    // Total revenue between dates
    @Query("""
        SELECT COALESCE(SUM(o.totalAmount), 0)
        FROM Order o
        WHERE o.status = :status
          AND o.createdAt >= :startDate
          AND o.createdAt < :endDate
    """)
    BigDecimal getTotalRevenue(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    // Count paid orders between dates
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.status = :status
          AND o.createdAt >= :startDate
          AND o.createdAt < :endDate
    """)
    Long countPaidOrders(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}