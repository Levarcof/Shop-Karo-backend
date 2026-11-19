package ecommerce.app.Ecommerce.listener;

import com.razorpay.RazorpayClient;
import com.razorpay.Refund;
import ecommerce.app.Ecommerce.entity.Order;
import ecommerce.app.Ecommerce.entity.Payment;
import ecommerce.app.Ecommerce.entity.RefundOrderItem;
import ecommerce.app.Ecommerce.enums.PaymentStatus;
import ecommerce.app.Ecommerce.enums.RefundStatus;
import ecommerce.app.Ecommerce.event.RefundOrderItemsEvent;
import ecommerce.app.Ecommerce.event.RefundRequestedEvent;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.OrderRepository;
import ecommerce.app.Ecommerce.repository.PaymentRepository;
import ecommerce.app.Ecommerce.repository.RefundOrderItemRepository;
import ecommerce.app.Ecommerce.service.RefundService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
public class RefundOrderItemsEventListener {
    private static final Logger log =
            LoggerFactory.getLogger(RefundOrderItemsEventListener.class);

    private final RazorpayClient razorpay;
    private final PaymentRepository paymentRepository;
    private final RefundOrderItemRepository refundOrderItemRepository;

    public RefundOrderItemsEventListener(RazorpayClient razorpay, PaymentRepository paymentRepository, RefundOrderItemRepository refundOrderItemRepository)   {
        this.razorpay = razorpay;
        this.paymentRepository = paymentRepository;
        this.refundOrderItemRepository = refundOrderItemRepository;
    }


    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRefundRequested(RefundOrderItemsEvent e) {
        processRefund(e.refundId() , e.amountPaise() , e.razorpayPaymentId());
    }

    public void processRefund(Long refundRowId, long amountPaise ,String razorpayPaymentId ) {
        RefundOrderItem refu = refundOrderItemRepository.findById(refundRowId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Refund not found")
                );
        if (refu.getStatus() != RefundStatus.PENDING) {
            log.info("Refund already processed or in progress: {}",
                    refundRowId);
            return;
        }
        refu.setStatus(RefundStatus.PROCESSING);
        refundOrderItemRepository.saveAndFlush(refu);

        String refundId;
        try {
            JSONObject req = new JSONObject();
            req.put("amount", amountPaise);
            Refund refund = razorpay.payments.refund(razorpayPaymentId, req);
            refundId = refund.get("id");
        } catch (Exception ex) {
            log.error("REFUND FAILED payment={} amount={}", refundRowId, amountPaise, ex);
            refu.setStatus(RefundStatus.FAILED);
            refundOrderItemRepository.save(refu);
            return;
        }

        try {
            refu.setRazorpayRefundId(refundId);
            refu.setStatus(RefundStatus.PROCESSING);
            refundOrderItemRepository.save(refu);
            log.info("Refund done payment={} refund={}", refundRowId, refundId);
        } catch (Exception ex) {
            // Razorpay me refund ho chuka hai, bas DB update fail hua
            log.error("REFUND DONE but DB update failed payment={} refund={}", refundRowId, refundId, ex);
        }

    }
}
