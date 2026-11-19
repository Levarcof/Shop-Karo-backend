package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.entity.Order;
import ecommerce.app.Ecommerce.enums.PaymentMethod;
import ecommerce.app.Ecommerce.enums.PaymentStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public class PaymentDetailDto {
    private Long id;
    private PaymentMethod method;
    private PaymentStatus status;
    private Long amountPaise;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private LocalDateTime paidAt;
    private String failureReason;
    private long refundAmountPaise;





    public PaymentDetailDto(Long id, PaymentMethod method, PaymentStatus status, Long amountPaise, String razorpayOrderId, String razorpayPaymentId, LocalDateTime paidAt, String failureReason, long refundAmountPaise) {
        this.id = id;
        this.method = method;
        this.status = status;
        this.amountPaise = amountPaise;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.paidAt = paidAt;
        this.failureReason = failureReason;
        this.refundAmountPaise = refundAmountPaise;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Long getAmountPaise() {
        return amountPaise;
    }

    public void setAmountPaise(Long amountPaise) {
        this.amountPaise = amountPaise;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public long getRefundAmountPaise() {
        return refundAmountPaise;
    }

    public void setRefundAmountPaise(long refundAmountPaise) {
        this.refundAmountPaise = refundAmountPaise;
    }
}
