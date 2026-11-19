package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.PaymentResult;
import ecommerce.app.Ecommerce.entity.Payment;
import ecommerce.app.Ecommerce.enums.*;
import ecommerce.app.Ecommerce.event.OrderPlacedEvent;
import ecommerce.app.Ecommerce.event.RefundRequestedEvent;
import ecommerce.app.Ecommerce.repository.OrderRepository;
import ecommerce.app.Ecommerce.repository.PaymentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {

    private PaymentRepository paymentRepository;
    private OrderRepository orderRepository;
    private ApplicationEventPublisher events;
    private RefundService refundService;
    private OrderService orderService;
    private NotificationService notificationService;

    private static final Logger log =
            LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, ApplicationEventPublisher events , RefundService refundService , OrderService orderService , NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.events = events;
        this.refundService = refundService;
        this.orderService = orderService;
        this.notificationService = notificationService;
    }

    @Transactional //(readOnly = true)
    public Optional<Payment> findByOrderId(Long orderId){
        return  paymentRepository.findFirstByOrderIdOrderByIdDesc(orderId);
    }

    @Transactional
    public  void attachRazorpayOrder(Long OrderId , String razorpayOrderId){
        Payment payment = paymentRepository.findFirstByOrderIdOrderByIdDesc(OrderId).orElseThrow();
        payment.setRazorpayOrderId(razorpayOrderId);
    }

    @Transactional
    public PaymentResult confirmPayment(String rzpOrderId, String rzpPaymentId, Long paidPaise){
        Payment payment = paymentRepository.findByRazorpayOrderId(rzpOrderId).orElseThrow(()-> new IllegalArgumentException("Unknown razorpay order"));
        Long orderId = payment.getOrder().getId();
        Long userId = payment.getOrder().getUser().getId();

        if(paidPaise != null && !paidPaise.equals(payment.getAmountPaise())){
            log.error("AMOUNT MISMATCH rzpOrder={} expected={} got={}",
                    rzpOrderId, payment.getAmountPaise(), paidPaise);

            int won = paymentRepository.markRefundPendingFromCreated(rzpOrderId , rzpPaymentId , paidPaise , "AMOUNT_MISMATCH" , PaymentStatus.CREATED , PaymentStatus.REFUND_PENDING);
            if(won == 1){
                orderService.cancelAndRelease(orderId , OrderStatus.PAYMENT_FAILED);
                notificationService.createNotification(NotificationType.PAYMENT_FAILED , userId , RecipientType.USER , ReferenceType.REFUND , payment.getId() ,
                        "Payment failled" , "Your Order "+orderId+" payment failled" , null );
                events.publishEvent(new RefundRequestedEvent(rzpPaymentId, paidPaise));
            }

            return  new PaymentResult(orderId , orderRepository.findStatusById(orderId));
        }

        int won = paymentRepository.markPaid(rzpOrderId, rzpPaymentId,
                PaymentStatus.CREATED, PaymentStatus.PAID, LocalDateTime.now());
        if(won == 0){
            return  new PaymentResult(orderId , orderRepository.findStatusById(orderId));
        }

        int  moved = orderRepository.transition(orderId , OrderStatus.PAYMENT_PENDING, OrderStatus.PLACED);
        if(moved == 0){
            events.publishEvent(new RefundRequestedEvent(rzpPaymentId, payment.getAmountPaise()));
        }
        else {
            events.publishEvent(new OrderPlacedEvent(orderId));
        }

        notificationService.createNotification(NotificationType.PAYMENT_SUCCESS ,userId , RecipientType.USER , ReferenceType.PAYMENT  , payment.getId() ,
                "Payment Successfully done" , "Your order "+orderId + " Payment successfully done" , null);

        notificationService.createNotification(NotificationType.PAYMENT_SUCCESS ,1L , RecipientType.ADMIN , ReferenceType.PAYMENT  , payment.getId() ,
                "Payment Recieve done" , " order "+orderId + " Payment recieve successfully done" , null);

        notificationService.createNotification(NotificationType.ORDER_PLACED , userId , RecipientType.USER , ReferenceType.ORDER ,
                orderId, "Placed Order" ,  "Your order #" + orderId + " has been placed.", null);

        notificationService.createNotification(NotificationType.ORDER_PLACED , 1L , RecipientType.ADMIN, ReferenceType.ORDER ,
               orderId, "Placed Order" ,  "Order " + orderId + " has been placed.", null);

        return  new PaymentResult(orderId , orderRepository.findStatusById(orderId));
    }

}
