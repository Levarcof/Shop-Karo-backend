package ecommerce.app.Ecommerce.entity;

import ecommerce.app.Ecommerce.enums.RefundStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class RefundOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    private String razorpayRefundId;

    private BigDecimal amount;

    private String reason;

    @Enumerated(EnumType.STRING)
    private RefundStatus status;

    private LocalDateTime createdAt;










    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getRazorpayRefundId() {
        return razorpayRefundId;
    }

    public void setRazorpayRefundId(String razorpayRefundId) {
        this.razorpayRefundId = razorpayRefundId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public RefundStatus getStatus() {
        return status;
    }

    public void setStatus(RefundStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
