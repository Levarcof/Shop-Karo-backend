package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.*;
import ecommerce.app.Ecommerce.entity.CustomUserDetails;
import ecommerce.app.Ecommerce.enums.OrderStatus;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.OrderRepository;
import ecommerce.app.Ecommerce.repository.UserRepository;
import ecommerce.app.Ecommerce.service.CheckoutService;
import ecommerce.app.Ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private OrderService orderService;
    private CheckoutService checkoutService;
    private OrderRepository orderRepository;
    private UserRepository userRepository;

    public OrderController(OrderService orderService, CheckoutService checkoutService, OrderRepository orderRepository , UserRepository userRepository) {
        this.orderService = orderService;
        this.checkoutService = checkoutService;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private Long currentUserId(Authentication auth){
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String email = jwtAuth.getToken().getSubject();

            return userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found")
                    )
                    .getId();
        }

        throw new IllegalStateException("JWT authentication required");
    }

    @PostMapping("/checkout")
    public  ResponseEntity<CheckoutResponse> checkOut(@Valid @RequestBody CheckoutRequest req ,
                                                      @RequestHeader("Idempotency-Key") String idemKey ,
                                                      Authentication auth){
        return ResponseEntity.ok(checkoutService.checkout(currentUserId(auth) , req , idemKey));
    }
    // API 4: user ne popup band kiya, stock turant release
    @PostMapping("/cancle-payment/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long id , Authentication auth){
        orderService.cancelPendingByUser(currentUserId(auth) , id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Page<Ordersesponse> myOrders(Authentication auth,
                                        @PageableDefault(size = 10) Pageable pageable) {
        List<OrderStatus> visible = List.of(OrderStatus.PLACED, OrderStatus.CONFIRMED,
                OrderStatus.SHIPPED, OrderStatus.DELIVERED, OrderStatus.CANCELLED);
//        return orderRepository.findSummaries(currentUserId(auth), visible, pageable);
        return orderService.getMyOrders(currentUserId(auth), visible, pageable);
    }


    // API 6: status polling
    @GetMapping("/{id}/status")
    public OrderStatusResponse status(@PathVariable Long id, Authentication auth) {
        OrderStatus s = orderRepository.findStatusByIdAndUser(id, currentUserId(auth))
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return new OrderStatusResponse(id, s);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderSummaryResponse> getOrder(@PathVariable Long id  , Authentication auth){
        return ResponseEntity.ok(orderService.getOrder(id , currentUserId(auth) ));
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id , Authentication auth){
        return ResponseEntity.ok(orderService.CancelOrder(id , currentUserId(auth)));
    }

    @PatchMapping("/partialDelete")
    public  ResponseEntity<String> partialDelete( Authentication auth , @RequestBody PartialOrderDeleteDto partialOrderDeleteDto ){
        return ResponseEntity.ok(orderService.partialOrderDelete(currentUserId(auth) , partialOrderDeleteDto));
    }



}
