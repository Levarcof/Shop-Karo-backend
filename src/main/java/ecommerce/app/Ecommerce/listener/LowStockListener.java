package ecommerce.app.Ecommerce.listener;

import ecommerce.app.Ecommerce.entity.Product;
import ecommerce.app.Ecommerce.enums.NotificationType;
import ecommerce.app.Ecommerce.enums.RecipientType;
import ecommerce.app.Ecommerce.enums.ReferenceType;
import ecommerce.app.Ecommerce.event.LowStockEvent;
import ecommerce.app.Ecommerce.repository.ProductRepository;
import ecommerce.app.Ecommerce.service.NotificationService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class LowStockListener {
private ProductRepository productRepository;
private NotificationService notificationService;

    public LowStockListener(ProductRepository productRepository, NotificationService notificationService) {
        this.productRepository = productRepository;
        this.notificationService = notificationService;
    }


    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleLowStock(LowStockEvent event){
        Product product = productRepository.findById(event.productId())
                .orElseThrow(() ->
                        new RuntimeException("Product not found")
                );

        if (event.productId() != null){
            notificationService.createNotification(NotificationType.LOW_STOCK , 1L, RecipientType.ADMIN , ReferenceType.PRODUCT ,
                    product.getId(),  "Low Stock Alert!!" , "Product of id #"+product.getId()+" stock is less than "+product.getMinQuantity() ,null);
        }
    }
}
