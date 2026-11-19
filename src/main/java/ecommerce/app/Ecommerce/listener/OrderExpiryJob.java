package ecommerce.app.Ecommerce.listener;

import ecommerce.app.Ecommerce.enums.OrderStatus;
import ecommerce.app.Ecommerce.repository.OrderRepository;
import ecommerce.app.Ecommerce.service.OrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class OrderExpiryJob {
    private OrderRepository orderRepository;
    private OrderService orderService;

    public OrderExpiryJob(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Scheduled(fixedDelay = 60_000)
    public  void  expiresOrder(){
        List<Long> ids = orderRepository.findExpiredIds(OrderStatus.PAYMENT_PENDING ,  LocalDateTime.now() , PageRequest.of(0, 100));
        ids.forEach(id->orderService.cancelAndRelease(id , OrderStatus.EXPIRED));

    }
}
