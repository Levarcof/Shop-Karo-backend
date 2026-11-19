package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.Util.Money;
import ecommerce.app.Ecommerce.dtos.*;
import ecommerce.app.Ecommerce.entity.*;
import ecommerce.app.Ecommerce.enums.*;
import ecommerce.app.Ecommerce.event.LowStockEvent;
import ecommerce.app.Ecommerce.event.OrderPlacedEvent;
import ecommerce.app.Ecommerce.event.RefundOrderItemsEvent;
import ecommerce.app.Ecommerce.event.RefundRequestedEvent;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.*;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Set<OrderStatus> USER_PARTIAL_OK = EnumSet.of(OrderStatus.PLACED);
    private static final long USER_WINDOW_MINUTES = 30;
    private ProductRepository productRepository;
    private OrderRepository orderRepository;
    private OrderItemRepository orderItemRepository;
    private PaymentRepository paymentRepository;
    private UserRepository userRepository;
    private AddressRepository addressRepository;
    private ApplicationEventPublisher events;
    private RefundService refundService;
    private NotificationService notificationService;
    private  RefundOrderItemRepository refundOrderItemRepository;

    @Value("${app.order.expiry-minutes}")
    private long expiryMinutes;

    public OrderService(RefundOrderItemRepository refundOrderItemRepository ,  ProductRepository productRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, PaymentRepository paymentRepository, UserRepository userRepository, AddressRepository addressRepository, ApplicationEventPublisher events , RefundService refundService , NotificationService notificationService) {
        this.refundOrderItemRepository = refundOrderItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.events = events;
        this.refundService = refundService;
        this.notificationService = notificationService;
        //when spring create any class of bean then it will also do dependency injection in constructer so in this situation the
        //constructer parameter is always class objects (where these class beans also managed by spring) .
        //if we put varibale like String , long , Integer in constructer then this will show error.
    }

    @Transactional
    public Order createOrder(Long userId, CheckoutRequest req, String idemKey) {
        Optional<Order> existing = orderRepository.findByUserIdAndIdempotencyKey(userId, idemKey);
        if (existing.isPresent()) {
            return existing.get();
        }

        List<Long> ids = req.getItems().stream().map(OrderItemReq::getProductId).toList();
        if (new HashSet<>(ids).size() != ids.size()) {
            throw new IllegalArgumentException("Duplicate product in cart");
        }

        Map<Long, Product> products = productRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        if (products.size() != ids.size()) {
            throw new IllegalArgumentException("Invalid product in cart");
        }

        Order order = new Order();
        order.setUser(userRepository.getReferenceById(userId));
        order.setAddress(addressRepository.getReferenceById(req.getAddressId()));
        order.setCreatedAt(LocalDateTime.now());
        order.setIdempotencyKey(idemKey);

        List<OrderItemReq> sorted = req.getItems()
                .stream()
                .sorted(Comparator.comparing(OrderItemReq::getProductId)).toList();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemReq it : sorted) {
            Product product = products.get(it.getProductId());
            int previousStock = product.getStockQuantity();

            if (productRepository.reserveStock(product.getId(), it.getQuantity()) == 0) {
                throw new IllegalStateException("Out of stock " + product.getName());
            }

            int newStock = previousStock - it.getQuantity();
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setQuantity(it.getQuantity());
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            order.getItems().add(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())));

            if(product.getMinQuantity() != null && newStock < product.getMinQuantity() && previousStock >= product.getMinQuantity()){
                notificationService.createNotification(NotificationType.LOW_STOCK , 1L, RecipientType.ADMIN , ReferenceType.PRODUCT ,
                        product.getId(),  "Low Stock Alert!!" , "Product of id #"+product.getId()+" stock is less than "+product.getMinQuantity() ,null);
            }

            if(newStock == 0 ){
                notificationService.createNotification(NotificationType.OUT_OF_STOCK , 1L , RecipientType.ADMIN , ReferenceType.PRODUCT ,
                        product.getId(),  "Out of Stock Alert" , "Product of id #"+product.getId()+" is Out of Stock " , null);
            }
        }

        order.setTotalAmount(total);

        boolean cod = req.getPaymentMethod() == PaymentMethod.COD;
        order.setStatus(cod ? OrderStatus.PLACED : OrderStatus.PAYMENT_PENDING);
        order.setExpiresAt((cod ? null : LocalDateTime.now().plusMinutes(expiryMinutes)));
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.CREATED);
        payment.setMethod(req.getPaymentMethod());
        payment.setAmountPaise(Money.toPaise(total));
        paymentRepository.save(payment);



        if (cod) {
            notificationService.createNotification(NotificationType.ORDER_PLACED , userId , RecipientType.USER , ReferenceType.ORDER ,
                    order.getId(), "Placed Order" ,  "Your order #" + order.getId() + " has been placed.", null);
            notificationService.createNotification(NotificationType.ORDER_PLACED , 1L , RecipientType.ADMIN, ReferenceType.ORDER ,
                    order.getId(), "Placed Order" ,  "Order " + order.getId() + " has been placed.", null);

            events.publishEvent(new OrderPlacedEvent(order.getId()));
        }


        return order;
    }

    @Transactional //(readOnly = true)
    public Optional<Order> findByIdempotencyKey(Long userId, String key) {
        return orderRepository.findByUserIdAndIdempotencyKey(userId, key);
    }

    @Transactional
    public void  cancelAndRelease(Long orderId , OrderStatus target){
        if(orderRepository.transition(orderId , OrderStatus.PAYMENT_PENDING , target) == 0){
            return;
        }

        PaymentStatus status = (target == OrderStatus.EXPIRED ) ? PaymentStatus.EXPIRED : PaymentStatus.FAILED;
        paymentRepository.markStatusByOrder(orderId , PaymentStatus.CREATED  , status);

        orderItemRepository.findByOrderId(orderId).forEach(i ->{
                    Product product = productRepository.findByIdForUpdate(i.getProduct().getId())
                            .orElseThrow(() ->
                                    new RuntimeException("Product not found"));

                 Integer prevStock = product.getStockQuantity();
                 Integer newStock = product.getStockQuantity() + i.getQuantity();
                    product.setStockQuantity(newStock);

                 if (newStock >= product.getMinQuantity() && prevStock < product.getMinQuantity()){
                     notificationService.resolveLowStockNotification(product.getId());
                 }
                }
                );
    }

    @Transactional
    public void cancelPendingByUser(Long userId , Long orderId){
        if(!orderRepository.existsByIdAndUserId(orderId,userId)){
            throw new IllegalArgumentException("Order not found");
        }
        cancelAndRelease(orderId , OrderStatus.PAYMENT_FAILED);
    }

    public  OrderSummaryResponse getOrder(Long id , Long userId ) {
        Order order = orderRepository.findByIdAndUserId(id , userId).orElseThrow(()->new ResourceNotFoundException("Order not found"));
        List<OrderItemDto> products = new ArrayList<>();
        if(order.getItems() != null && !order.getItems().isEmpty()){
            for(OrderItem item : order.getItems()){
                Product product = item.getProduct();
                BigDecimal price = BigDecimal.valueOf(item.getQuantity()).multiply(item.getUnitPrice());
          products.add(new OrderItemDto(item.getId(), product.getId() , product.getName() , item.getQuantity(), price , product.getImageUrl() , item.getStatus()) );
            }
        }
        OrderSummaryResponse orderSummaryResponse = new OrderSummaryResponse(id , order.getStatus() , order.getTotalAmount() , order.getCreatedAt() , products );
        return orderSummaryResponse;
    }

    public Page<Ordersesponse> getMyOrders(Long id, List<OrderStatus> visible, Pageable pageable) {
      return orderRepository.findByUser_IdAndStatusIn(id , visible , pageable)
             .map(order -> {
                       Ordersesponse response =  new Ordersesponse(order.getId(), order.getStatus(), order.getTotalAmount(), order.getCreatedAt());
                       return response;
                     }
             );
    }

    @Transactional
    public  String CancelOrder(Long id , Long userId) {
        Order order = orderRepository.findByIdAndUserId(id , userId).orElseThrow(()->new ResourceNotFoundException("Order not found"));
        if(order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.CANCELLED  ){
            throw new IllegalStateException(
                    "Order cannot be cancelled in its current status");
        }

        boolean ableTocCancel = order.getCreatedAt().plusMinutes(30).isAfter(LocalDateTime.now());

        if(!ableTocCancel){
            throw new IllegalStateException(
                    "Order cancellation window has expired");
        }

        Optional<Payment> paidPayment = paymentRepository.findFirstByOrder_IdAndStatusOrderByIdDesc(id , PaymentStatus.PAID);
        Payment payment = null;

        if(paidPayment.isPresent()){
            payment = paidPayment.get();
            if(payment.getMethod() != PaymentMethod.COD && payment.getStatus()  == PaymentStatus.PAID){
                String paymentId = payment.getRazorpayPaymentId();
                if(paymentId == null || paymentId.isBlank()){
                    throw new IllegalStateException(
                            "Payment ID is missing; refund cannot be initiated");
                }
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        if(order.getItems() != null && !order.getItems().isEmpty()){
            for(OrderItem item : order.getItems()){
                productRepository.releaseStock(item.getProduct().getId(), item.getQuantity());
                item.setStatus(OrderItemStatus.CANCELLED);
            }
        }

        orderItemRepository.saveAll(order.getItems());


        if (payment != null &&  payment.getMethod() != PaymentMethod.COD && payment.getStatus() == PaymentStatus.PAID) {
            events.publishEvent(new RefundRequestedEvent(payment.getRazorpayPaymentId(), payment.getAmountPaise()));
        }

        notificationService.createNotification(NotificationType.ORDER_CANCELLED , userId , RecipientType.USER , ReferenceType.ORDER , id , "Cancel order" ,
                "Your order "+id+" has beem canceled" , null);

        notificationService.createNotification(NotificationType.ORDER_CANCELLED , 1L , RecipientType.ADMIN , ReferenceType.ORDER , id , "Cancel order" ,
                "order "+id+" has beem canceled" , null);

        return  "Order cancelled Successfully";
    }

    @Transactional
    public String partialOrderDelete(Long userId, PartialOrderDeleteDto dto) {
        Order order = orderRepository.findByIdAndUserIdForUpdate(dto.getOrderId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!USER_PARTIAL_OK.contains(order.getStatus())) {
            throw new IllegalStateException("Items can't be cancelled in the current order status");
        }
        if (order.getCreatedAt().plusMinutes(USER_WINDOW_MINUTES).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Order cancellation window has expired");
        }
        return cancelItems(order, dto.getOrderItemIds(), false);
    }

    public String cancelItems(Order order, List<Long> rawIds, boolean byAdmin) {
        if (rawIds == null || rawIds.isEmpty()) {
            throw new IllegalArgumentException("Select at least one item to cancel");
        }
        List<Long> ids = rawIds.stream().filter(Objects::nonNull).distinct().toList();

        List<OrderItem> selected = orderItemRepository.findByOrderIdAndIdIn(order.getId(), ids);
        if (selected.size() != ids.size()) {
            throw new IllegalArgumentException("Some items are invalid or don't belong to this order");
        }
        if (selected.stream().anyMatch(i -> i.getStatus() == OrderItemStatus.CANCELLED)) {
            throw new IllegalStateException("Some items are already cancelled");
        }

        long activeCount = order.getItems().stream()
                .filter(i -> i.getStatus() == OrderItemStatus.ACTIVE).count();
        if (selected.size() >= activeCount) {
            throw new IllegalStateException(
                    "You can't cancel every item. Cancel the whole order instead.");
        }

        // Payment must be valid BEFORE anything changes
        Payment paid = paymentRepository
                .findFirstByOrder_IdAndStatusOrderByIdDesc(order.getId(), PaymentStatus.PAID)
                .orElse(null);
        boolean needsRefund = paid != null && paid.getMethod() != PaymentMethod.COD;
        if (needsRefund && (paid.getRazorpayPaymentId() == null || paid.getRazorpayPaymentId().isBlank())) {
            throw new IllegalStateException("Payment ID is missing; refund cannot be initiated");
        }

        BigDecimal refund = BigDecimal.ZERO;
        for (OrderItem item : selected) {
            if (item.getStatus() == OrderItemStatus.CANCELLED) continue;
            refund = refund.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            productRepository.releaseStock(item.getProduct().getId(), item.getQuantity());
            item.setStatus(OrderItemStatus.CANCELLED);
        }

        order.setTotalAmount(order.getTotalAmount().subtract(refund));
        order.setUpdatedAt(LocalDateTime.now());
        orderItemRepository.saveAll(selected);

        if (needsRefund) {
            long paise = refund.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
            RefundOrderItem refundItemMoney = new RefundOrderItem();
            refundItemMoney.setOrder(order);
            refundItemMoney.setPayment(paid);
            refundItemMoney.setAmount(    BigDecimal.valueOf(paise)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.UNNECESSARY));
            refundItemMoney.setStatus(RefundStatus.PENDING);
            refundItemMoney.setCreatedAt(LocalDateTime.now());

            refundOrderItemRepository.save(refundItemMoney);
            events.publishEvent(new RefundOrderItemsEvent(refundItemMoney.getId() , paise , paid.getRazorpayPaymentId()));
        }

        notifyPartialCancel(order, byAdmin);
        return "Order items cancelled successfully";
    }

    private void notifyPartialCancel(Order order, boolean byAdmin) {
        try {
            notificationService.createNotification(NotificationType.ORDER_PARTIALLY_CANCELLED,
                    order.getUser().getId(), RecipientType.USER, ReferenceType.ORDER, order.getId(),
                    "Order items cancelled",
                    "Some items in order #" + order.getId() + (byAdmin ? " were cancelled by our team." : " were cancelled."),
                    null);
            notificationService.createNotification(NotificationType.ORDER_PARTIALLY_CANCELLED,
                    1L, RecipientType.ADMIN, ReferenceType.ORDER, order.getId(),
                    "Order items cancelled",
                    "Some items in order #" + order.getId() + " were cancelled.", null);
        } catch (Exception e) {
            System.out.println("Partial cancel notification failed for order {}"+ order.getId()+e);
        }
    }
}
