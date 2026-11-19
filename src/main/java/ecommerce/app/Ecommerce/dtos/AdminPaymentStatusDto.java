package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.PaymentStatus;

public class AdminPaymentStatusDto {
    private  Long id;
    private PaymentStatus paymentStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
