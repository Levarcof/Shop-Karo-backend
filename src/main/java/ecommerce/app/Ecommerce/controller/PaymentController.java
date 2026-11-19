package ecommerce.app.Ecommerce.controller;

import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import ecommerce.app.Ecommerce.dtos.PaymentResult;
import ecommerce.app.Ecommerce.dtos.VerifyPaymentRequest;
import ecommerce.app.Ecommerce.dtos.VerifyPaymentResponse;
import ecommerce.app.Ecommerce.enums.OrderStatus;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.UserRepository;
import ecommerce.app.Ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    private PaymentService paymentService;
    private RazorpayClient razorpayClient;
    private UserRepository userRepository;


    @Value("${razorpay.key.secret}")
    private String keySecret;

    public PaymentController(PaymentService paymentService , RazorpayClient razorpayClient , UserRepository userRepository) {
        this.razorpayClient = razorpayClient;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
    }

    @PostMapping("/verify")
    public ResponseEntity<VerifyPaymentResponse> verify(@Valid @RequestBody VerifyPaymentRequest req , Authentication auth) throws Exception{
        JSONObject a = new JSONObject();
        a.put("razorpay_order_id" , req.getRazorpayOrderId());
        a.put("razorpay_payment_id" , req.getRazorpayPaymentId());
        a.put("razorpay_signature" , req.getRazorpaySignature());

        if(!Utils.verifyPaymentSignature(a , keySecret)){
            throw  new SecurityException("Invalid payment signature");
        }

        com.razorpay.Payment rzpPayment = razorpayClient.payments.fetch(req.getRazorpayPaymentId());
        long paidPaise = ((Number) rzpPayment.get("amount")).longValue();

        PaymentResult result = paymentService.confirmPayment(req.getRazorpayOrderId() , req.getRazorpayPaymentId() , paidPaise);

        String message = (result.getOrderStatus() == OrderStatus.PLACED || result.getOrderStatus() == OrderStatus.CONFIRMED)
                ?"Payment succesfull , order placed" : "Payment received but order had expired. Refund will be processed";

        return ResponseEntity.ok(new VerifyPaymentResponse(result.getOrderId() , result.getOrderStatus(), message));

    }
}
