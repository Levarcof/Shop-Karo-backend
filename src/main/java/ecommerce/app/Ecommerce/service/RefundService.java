package ecommerce.app.Ecommerce.service;

import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import ecommerce.app.Ecommerce.enums.PaymentStatus;
import ecommerce.app.Ecommerce.event.RefundRequestedEvent;
import ecommerce.app.Ecommerce.repository.OrderRepository;
import ecommerce.app.Ecommerce.repository.PaymentRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
public class RefundService {
    private static final Logger log =
            LoggerFactory.getLogger(RefundService.class);

    private final   RazorpayClient razorpay;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public RefundService(RazorpayClient razorpay, PaymentRepository paymentRepository , OrderRepository orderRepository) {
        this.razorpay = razorpay;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRefundRequested(RefundRequestedEvent e) {
        processRefund(e.razorpayPaymentId(), e.amountPaise());
    }

    @Transactional
    public void processRefund(String paymentId, long amountPaise) {
        String refundId;
        try {
            JSONObject req = new JSONObject();
            req.put("amount", amountPaise);
            Refund refund = razorpay.payments.refund(paymentId, req);
            refundId = refund.get("id");
        } catch (Exception ex) {
            log.error("REFUND FAILED payment={} amount={}", paymentId, amountPaise, ex);
            paymentRepository.markStatusByPaymentId(paymentId, PaymentStatus.REFUND_FAILED);
            return;
        }

        try {
            paymentRepository.markRefunded(paymentId);
            log.info("Refund done payment={} refund={}", paymentId, refundId);
        } catch (Exception ex) {
            // Razorpay me refund ho chuka hai, bas DB update fail hua
            log.error("REFUND DONE but DB update failed payment={} refund={}", paymentId, refundId, ex);
        }

    }
}
