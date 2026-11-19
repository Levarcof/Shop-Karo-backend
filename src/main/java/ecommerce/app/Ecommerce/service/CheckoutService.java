package ecommerce.app.Ecommerce.service;

import com.razorpay.RazorpayClient;
import ecommerce.app.Ecommerce.Util.Money;
import ecommerce.app.Ecommerce.dtos.CheckoutRequest;
import ecommerce.app.Ecommerce.dtos.CheckoutResponse;
import ecommerce.app.Ecommerce.entity.Order;
import ecommerce.app.Ecommerce.entity.Payment;
import ecommerce.app.Ecommerce.entity.Product;
import ecommerce.app.Ecommerce.enums.*;
import ecommerce.app.Ecommerce.repository.ProductRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class CheckoutService {
    private  OrderService orderService;
    private  PaymentService paymentService;
private RazorpayClient razorpay;

    @Value("${razorpay.key.id}")
    private String keyId;

    public CheckoutService(OrderService orderService, RazorpayClient razorpay , PaymentService paymentService) {
        this.orderService = orderService;
        this.razorpay = razorpay;
        this.paymentService = paymentService;
    }

    public CheckoutResponse checkout(Long userId , CheckoutRequest req , String idemKey){
        Order order;
        try{
            order = orderService.createOrder(userId , req , idemKey);
        }
        catch (DataIntegrityViolationException e){
            order = orderService.findByIdempotencyKey(userId , idemKey).orElseThrow(() -> e);
        }

        // Step 2: COD ho gaya
        if (req.getPaymentMethod() == PaymentMethod.COD) {
            return CheckoutResponse.cod(order.getId());
        }

        // Step 3: Online, par order pehle hi aage badh chuka ho (retry/replay case)
        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            return CheckoutResponse.status(order.getId(), order.getStatus(), PaymentMethod.ONLINE);
        }

        Payment existing = paymentService.findByOrderId(order.getId()).orElseThrow();

        if(existing.getRazorpayOrderId() != null){
            return CheckoutResponse.online(order.getId() , existing.getRazorpayOrderId() , existing.getAmountPaise(),  keyId);
        }

        long paisa = Money.toPaise(order.getTotalAmount());

        try{
            JSONObject option = new JSONObject();
            option.put("amount" , paisa);
            option.put("currency" ,"INR");
            option.put("receipt" , "order_" + order.getId());

            com.razorpay.Order rzp  = razorpay.orders.create(option);
            String razorpayOrderId = rzp.get("id");
            paymentService.attachRazorpayOrder(order.getId(),  razorpayOrderId);

            return CheckoutResponse.online(order.getId(),  razorpayOrderId , paisa , keyId);

        }
        catch (Exception e){
//            log.error("Razorpay order creation failed for order {}", order.getId(), e);
            orderService.cancelAndRelease(order.getId(), OrderStatus.PAYMENT_FAILED);
            throw new IllegalStateException("Paymentgateway error , please try again");
        }

    }
}
