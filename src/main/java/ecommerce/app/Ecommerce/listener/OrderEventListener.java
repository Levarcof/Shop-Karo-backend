package ecommerce.app.Ecommerce.listener;

import ecommerce.app.Ecommerce.event.OrderPlacedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class OrderEventListener {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void  onOrderPlaced(OrderPlacedEvent e){
        // Yahan: confirmation email/SMS, invoice PDF, seller notification
//        log.info("Order {} placed, sending notifications", e.orderId());
    }
}
