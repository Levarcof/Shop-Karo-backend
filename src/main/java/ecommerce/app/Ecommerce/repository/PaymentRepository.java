package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.Payment;
import ecommerce.app.Ecommerce.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment , Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = :to WHERE p.order.id = :orderId AND p.status = :from")
    int markStatusByOrder(@Param("orderId") Long orderId,
                          @Param("from") PaymentStatus from, @Param("to") PaymentStatus to);

    Optional<Payment> findFirstByOrderIdOrderByIdDesc(Long orderId);

    Optional<Payment> findByRazorpayOrderId(String rzpOrderId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Payment p SET p.status = :to, p.razorpayPaymentId = :pid, p.paidAt = :now
        WHERE p.razorpayOrderId = :oid AND p.status = :from
        """)
    int markPaid(@Param("oid") String oid, @Param("pid") String pid,
                 @Param("from") PaymentStatus from, @Param("to") PaymentStatus to,
                 @Param("now") LocalDateTime now);



    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status =  ecommerce.app.Ecommerce.enums.PaymentStatus.REFUNDED WHERE p.razorpayPaymentId = :pid")
    int markRefunded(@Param("pid") String pid);


    @Modifying
    @jakarta.transaction.Transactional
    @Query("""
    UPDATE Payment p SET p.status = :to, p.razorpayPaymentId = :pid,
           p.failureReason = :reason, p.refundAmountPaise = :amount
    WHERE p.razorpayOrderId = :oid AND p.status = :from
    """)
    int markRefundPendingFromCreated(@Param("oid") String oid, @Param("pid") String pid,
                                     @Param("amount") long amount, @Param("reason") String reason,
                                     @Param("from") PaymentStatus from, @Param("to") PaymentStatus to);

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = :to WHERE p.razorpayPaymentId = :pid")
    int markStatusByPaymentId(@Param("pid") String pid, @Param("to") PaymentStatus to);

    Optional<Payment> findFirstByOrder_IdAndStatusOrderByIdDesc(
            Long orderId,
            PaymentStatus status
    );

    List<Payment> findAllByOrder_IdOrderByIdDesc(Long orderId);


    //Wheather you like it or not , i do my besy
}
