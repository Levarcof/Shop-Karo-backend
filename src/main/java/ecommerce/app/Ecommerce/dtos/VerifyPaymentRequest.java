package ecommerce.app.Ecommerce.dtos;

import jakarta.validation.constraints.NotBlank;

public class VerifyPaymentRequest {
    @NotBlank
    private String razorpayOrderId;

    @NotBlank
    private String razorpayPaymentId;

    @NotBlank
    private String razorpaySignature;







    public @NotBlank String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(@NotBlank String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public @NotBlank String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(@NotBlank String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public @NotBlank String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(@NotBlank String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }
}
