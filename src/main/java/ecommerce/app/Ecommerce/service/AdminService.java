package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.*;
import ecommerce.app.Ecommerce.entity.*;
import ecommerce.app.Ecommerce.enums.*;
import ecommerce.app.Ecommerce.event.RefundRequestedEvent;
import ecommerce.app.Ecommerce.exception.BadRequestException;
import ecommerce.app.Ecommerce.exception.ResourceAlreadyExist;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.*;
import org.jspecify.annotations.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {

    private static final Set<OrderStatus> ADMIN_PARTIAL_OK =
            EnumSet.of(OrderStatus.PLACED, OrderStatus.CONFIRMED, OrderStatus.PROCESSING);

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private OrderRepository orderRepository;
    private PaymentRepository paymentRepository;
    private NotificationService notificationService;
    private NotificationRepository notificationRepository;
    private ReviewRepository reviewRepository;
    private ReviewService reviewService;
    private OrderItemRepository orderItemRepository;
    private OrderService orderService;
    private static final int MAX_PAGE_SIZE = 100;
    private ApplicationEventPublisher events;

    private static final Map<String, String> SORT_FIELDS = Map.of(
            "createdAt", "createdAt",
            "totalAmount", "totalAmount",
            "orderStatus", "orderStatus"
    );

    public AdminService(OrderService orderService ,  OrderItemRepository orderItemRepository, ProductRepository productRepository , CategoryRepository categoryRepository , OrderRepository orderRepository , PaymentRepository paymentRepository , ApplicationEventPublisher events , NotificationService notificationService , NotificationRepository notificationRepository , ReviewRepository reviewRepository , ReviewService reviewService) {
       this.orderService = orderService;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.paymentRepository = paymentRepository;
        this.events = events;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
    }

    @Transactional
    public ProductResponseDto addProduct(ProductReqDto productReqDto) {
        boolean exist = productRepository.existsBySku(productReqDto.getSku());
        if(exist){
            throw new ResourceAlreadyExist("Product is alredy exist");
        }

        Category category = categoryRepository.findById(productReqDto.getCategory_id()).orElseThrow(()->new ResourceNotFoundException("Category not found"));
        Product product = new Product();
        product.setActive(true);
        product.setName(productReqDto.getName());
        product.setDescription(productReqDto.getDescription());
        product.setPrice(productReqDto.getPrice());
        product.setCreatedAt(LocalDateTime.now());
        product.setStockQuantity(productReqDto.getStockQuantity());
        product.setUpdatedAt(LocalDateTime.now());
        product.setImageUrl(productReqDto.getImageUrl());
        product.setCategory(category);
        product.setSku(productReqDto.getSku());
        product.setMinQuantity(productReqDto.getMinQuantity());

        productRepository.save(product);

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setName(productReqDto.getName());
        productResponseDto.setStatus("Product save successfully");

        notificationService.createNotification(NotificationType.PRODUCT_ADD , 1L , RecipientType.ADMIN , ReferenceType.PRODUCT , product.getId(),
                "Product add" , "Product "+product.getId()+" added successfully" , null);

        return productResponseDto;

    }

    @Transactional
    public CategoryResponseDto addCategory(CategoryReqDto categoryReqDto) {
        boolean isExist = categoryRepository.existsByName(categoryReqDto.getName());
        if(isExist){
            throw new ResourceAlreadyExist("Category alredy exist");
        }
        Category category = new Category();
        category.setCreatedAt(LocalDateTime.now());
        category.setDescription(categoryReqDto.getDescription());
        category.setName(categoryReqDto.getName());
        category.setUpdatedAt(LocalDateTime.now());

        categoryRepository.save(category);

        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setName(categoryReqDto.getName());
        categoryResponseDto.setStatus("Category added successfully");

        notificationService.createNotification(NotificationType.CATEGORY_ADD , 1L , RecipientType.ADMIN ,  ReferenceType.CATEGORY ,
                category.getId(),  "Category added" , "Category "+category.getId() + "added successfully" , null);

        return categoryResponseDto;

    }

    @Transactional
    public ProductResponseDto deleteProduct(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty()){
            throw  new ResourceNotFoundException("Product not found so it's not able to delete");
        }
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setName(product.get().getName());
        productResponseDto.setStatus("Deleted successfully");

        productRepository.deleteById(id);

        notificationService.createNotification(NotificationType.DELETED_PRODUCT , 1L , RecipientType.ADMIN ,  ReferenceType.PRODUCT ,
               id,  "Deleted Product" , "Product "+id + "deleted successfully" , null);

        return productResponseDto;

    }

    @Transactional
    public CategoryResponseDto deleteCategory(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isEmpty()){
            throw  new ResourceNotFoundException("Category not found so it's not able to delete");
        }
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setName(category.get().getName());
        categoryResponseDto.setStatus("Deleted successfully");

        categoryRepository.deleteById(id);

        notificationService.createNotification(NotificationType.DELETED_CATEGORY , 1L , RecipientType.ADMIN ,  ReferenceType.CATEGORY ,
                id,  "Deleted Category" , "Category "+id + "deleted successfully" , null);

        return categoryResponseDto;
    }

    @Transactional
    public ProductResponseDto updateProduct(ProductReqDto productReqDto) {
        Product product = productRepository.findBySku(productReqDto.getSku())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found , so that update not possible"));

        int preStock = product.getStockQuantity();
        int newStock = productReqDto.getStockQuantity();

// Category sirf tab badlo jab id aayi ho
        if (productReqDto.getCategory_id() != null) {
            Category category = categoryRepository.findById(productReqDto.getCategory_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            product.setCategory(category);
        }

        if (productReqDto.getName() != null)        product.setName(productReqDto.getName());
        if (productReqDto.getDescription() != null) product.setDescription(productReqDto.getDescription());
        if (productReqDto.getPrice() != null)       product.setPrice(productReqDto.getPrice());
        if (productReqDto.getImageUrl() != null)    product.setImageUrl(productReqDto.getImageUrl());
        if (productReqDto.getStockQuantity() != null) product.setStockQuantity(productReqDto.getStockQuantity());
        if (productReqDto.getMinQuantity() != null) product.setMinQuantity(productReqDto.getMinQuantity());

        productRepository.save(product);

        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setName(product.getName());
        productResponseDto.setStatus("Product update successfully");

        if(preStock < product.getMinQuantity() && newStock >= product.getMinQuantity() ){
            notificationService.resolveLowStockNotification(product.getId());
        }

        if(preStock == 0){
            notificationService.resolveOutOfStockNotification(product.getId());
        }

        if(newStock < product.getMinQuantity()){
            notificationService.createNotification(NotificationType.LOW_STOCK , 1L , RecipientType.ADMIN ,
                    ReferenceType.PRODUCT , product.getId(), "Low stock Alert" , "Product of id #"+product.getId()+" stock is less than "+product.getMinQuantity() , null);

        }

        notificationService.createNotification(NotificationType.UPDATE_PRODUCT , 1L , RecipientType.ADMIN , ReferenceType.PRODUCT ,
                product.getId() , "Product update" , "Product "+product.getId()+" updated  successfully" , null);

        return productResponseDto;
    }

    @Transactional
    public PagedResponse<AdminOrderResponseDto> getorders(OrderFilterRequest filter ){
        validateFilters(filter);

        Pageable pageable = createPageable(filter);

        LocalDateTime startInclusive = null;
        LocalDateTime endExclusive = null;

        if (filter.getStartDate() != null) {

            startInclusive = filter.getStartDate()
                    .atStartOfDay();
        }

        if (filter.getEndDate() != null) {

            endExclusive = filter.getEndDate()
                    .plusDays(1)
                    .atStartOfDay();
        }

        Page<Order> orderPage = orderRepository.findAll(
                OrderSpecifications.withFilters(
                        filter,
                        startInclusive,
                        endExclusive
                ),
                pageable
        );

        Page<AdminOrderResponseDto> responsePage = orderPage.map(this::toSummaryResponse);

        return  PagedResponse.from(responsePage);
    }

    private AdminOrderResponseDto toSummaryResponse(Order order){
        User user = order.getUser();
        return new AdminOrderResponseDto(
                order.getId(), user.getId(), user.getName(), user.getEmail(), order.getTotalAmount() , order.getStatus(), order.getCreatedAt()
        );
    }

    private Pageable createPageable(OrderFilterRequest filter){
        if(filter.getPage() < 0){
            throw  new IllegalArgumentException("Page number can not be negative");
        }

        if (filter.getSize() < 1 || filter.getSize() > MAX_PAGE_SIZE){
            throw  new IllegalArgumentException("Page size is incorrect");
        }

        String reqSort = filter.getSortBy();

        if(reqSort == null || !SORT_FIELDS.containsKey(reqSort) ){
            throw  new IllegalArgumentException(     "Unsupported sort field. Allowed fields: "
                    + SORT_FIELDS.keySet());
        }

        Sort.Direction direction;
        try{
            direction = Sort.Direction.fromString(filter.getDirection());
        }
        catch (IllegalArgumentException | NullPointerException  ex){
            throw  new IllegalArgumentException(  "Direction must be ASC or DESC");
        }
        String entityField = SORT_FIELDS.get(reqSort);

        Sort sort = Sort.by(direction ,entityField )
                .and(Sort.by(Sort.Direction.DESC ,  "id"));

        return  PageRequest.of(filter.getPage(), filter.getSize() , sort);

    }
    private void validateFilters(OrderFilterRequest filter){
        if(filter.getStartDate() != null && filter.getEndDate() != null && filter.getStartDate().isAfter(filter.getEndDate())){
            throw new IllegalArgumentException("startDate cannot be after endDate");
        }

        if(filter.getStartDate() != null && filter.getEndDate() != null && filter.getStartDate().plusDays(366).isBefore(filter.getEndDate())){
            throw new IllegalArgumentException("Enter range in out of range");
        }

        BigDecimal min = filter.getMinAmount();
        BigDecimal max = filter.getMaxAmount();

        if (min != null && min.signum() < 0) {
            throw new IllegalArgumentException("min must be gretor than 0");
        }

        if (max != null && max.signum() < 0) {
            throw new IllegalArgumentException("max must be gretor than 0");
        }

        if (min != null && max != null
                && min.compareTo(max) > 0) {

            throw new IllegalArgumentException("max can't be less than min");
        }
    }


    @Transactional
    public  AdminOrderDetails getOrderDetails(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid order ID");
        }

        Order order = orderRepository.findDetailsById(id).orElseThrow(()->new ResourceNotFoundException("Order not found"));
        List<Payment> payments = paymentRepository.findAllByOrder_IdOrderByIdDesc(id);

        List<PaymentDetailDto> paymentDetailDtoList = payments.stream().map(this::getPaymentDetailDto).toList();
        UserDetailDto userDetailDto = getUserDetailDto(order.getUser());
        List<OrderItemDto> products = new ArrayList<>();

        if(order.getItems() != null && !order.getItems().isEmpty()){
            for(OrderItem item : order.getItems()){
                Product product = item.getProduct();
                BigDecimal price = BigDecimal.valueOf(item.getQuantity()).multiply(item.getUnitPrice());
                products.add(new OrderItemDto(item.getId() ,  product.getId() , product.getName(), item.getQuantity(), price , product.getImageUrl() , item.getStatus()));
            }
        }

        AdminOrderDetails response = new AdminOrderDetails();
        response.setOrderId(order.getId());
        response.setOrderStatus(order.getStatus());
        response.setPayments(paymentDetailDtoList);
        response.setUser(userDetailDto);
        response.setProducts(products);
        response.setSubTotal(order.getTotalAmount());

        return response;
    }
    private UserDetailDto getUserDetailDto(User user){
    UserDetailDto userDetailDto = new UserDetailDto();
    userDetailDto.setEmail(user.getEmail());
    userDetailDto.setEnabled(user.isEnabled());
    userDetailDto.setId(user.getId());
    userDetailDto.setProfileImageUrl(user.getProfileImageUrl());
    userDetailDto.setCreated_at(user.getCreated_at());
    userDetailDto.setName(user.getName());

    return userDetailDto;
    }

    private PaymentDetailDto getPaymentDetailDto(Payment payment){
        return  new PaymentDetailDto(payment.getId() , payment.getMethod() , payment.getStatus() , payment.getAmountPaise() ,
                payment.getRazorpayOrderId() , payment.getRazorpayPaymentId() , payment.getPaidAt() , payment.getFailureReason() , payment.getRefundAmountPaise());

    }

    @Transactional
    public String updateOrderStatus(AdminOrderStatusDto dto) {

        if (dto.getId() == null || dto.getId() <= 0
                || dto.getOrderStatus() == null) {
            throw new IllegalArgumentException(
                    "Valid order ID and order status are required"
            );
        }

        Order order = orderRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = dto.getOrderStatus();

        if (currentStatus == newStatus) {
            return "Order already has this status";
        }

        if (currentStatus == OrderStatus.CANCELLED
                || currentStatus == OrderStatus.DELIVERED) {
            throw new IllegalStateException(
                    "Order status cannot be changed from " + currentStatus
            );
        }

        boolean validTransition = switch (currentStatus) {
            case PLACED ->
                    newStatus == OrderStatus.CONFIRMED
                            || newStatus == OrderStatus.PROCESSING;

            case CONFIRMED ->
                    newStatus == OrderStatus.PROCESSING;

            case PROCESSING ->
                    newStatus == OrderStatus.SHIPPED;

            case SHIPPED ->
                    newStatus == OrderStatus.DELIVERED;

            default -> false;
        };

        if (!validTransition) {
            throw new IllegalStateException(
                    "Invalid order status transition: "
                            + currentStatus + " -> " + newStatus
            );
        }

        order.setStatus(newStatus);
        NotificationType type = orderNotificationType(dto.getOrderStatus());

        if (type != null){

            notificationService.createNotification(type,  1L , RecipientType.ADMIN , ReferenceType.ORDER , order.getId(),
                    "Order state Update" , "Order "+order.getId() +" state updated" , null);

            notificationService.createNotification(type,  order.getUser().getId(), RecipientType.USER , ReferenceType.ORDER , order.getId(),
                    "Order state Update" , "Order "+order.getId() +" state updated" , null);

        }


        return "Order status updated successfully";
    }

    private NotificationType orderNotificationType(OrderStatus status) {
        return switch (status) {
            case PLACED -> NotificationType.ORDER_PLACED;
            case CONFIRMED -> NotificationType.ORDER_CONFIRMED;
            case SHIPPED -> NotificationType.ORDER_SHIPPED;
            case DELIVERED -> NotificationType.ORDER_DELIVERED;
            case CANCELLED -> NotificationType.ORDER_CANCELLED;
            default -> null; // PROCESSING, PAYMENT_PENDING: user ko notification nahi
        };
    }

    private NotificationType paymentNotificationType(PaymentStatus status) {
        return switch (status) {
            case PAID -> NotificationType.PAYMENT_SUCCESS;
            case REFUNDED -> NotificationType.REFUND_SUCCESS;
            case CREATED -> NotificationType.PAYMENT_PENDING;
            default -> null;
        };
    }

    @Transactional
    public String updatePaymentStatus(AdminPaymentStatusDto dto) {

        if (dto.getId() == null || dto.getId() <= 0
                || dto.getPaymentStatus() == null) {
            throw new IllegalArgumentException(
                    "Valid payment ID and payment status are required"
            );
        }

        Payment payment = paymentRepository.findById(dto.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found"));

        PaymentStatus currentStatus = payment.getStatus();
        PaymentStatus newStatus = dto.getPaymentStatus();

        if (currentStatus == newStatus) {
            return "Payment already has this status";
        }

        if (payment.getMethod() != PaymentMethod.COD) {
            throw new IllegalStateException(
                    "Online payment status must be updated through verified payment gateway events"
            );
        }

        boolean validTransition = switch (currentStatus){
            case CREATED ->
                newStatus == PaymentStatus.PAID;

            case PAID ->
                newStatus == PaymentStatus.REFUNDED && payment.getOrder().getStatus() == OrderStatus.CANCELLED;

            default ->
                false;
        };

        if(!validTransition){
            throw new IllegalStateException(
                    "Invalid state transition"
            );
        }

        payment.setStatus(newStatus);

        NotificationType notificationType = paymentNotificationType(dto.getPaymentStatus());
        if(notificationType != null){
            notificationService.createNotification(notificationType,  1L , RecipientType.ADMIN , ReferenceType.PAYMENT , payment.getId(),
                    "Payment state Update" , "payment of order #"+payment.getOrder().getId() +" state updated" , null);


            notificationService.createNotification(notificationType,  payment.getOrder().getUser().getId(), RecipientType.USER , ReferenceType.ORDER , payment.getId(),
                    "Payment state Update" , "payment of order # "+payment.getOrder().getId()+" state updated" , null);
        }

        return "COD payment State updated successfully";
    }

    @Transactional
    public  String cancelOrder(Long id) {
        Order order  = orderRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Order not found"));

        if(order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.CANCELLED ){
            throw new IllegalStateException(
                    "Order cannot be cancelled in its current status");
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
                Product product = productRepository.findByIdForUpdate(item.getProduct().getId())
                        .orElseThrow(() ->
                                new RuntimeException("Product not found"));

                int prevStock = product.getStockQuantity();
                int newStock = prevStock + item.getQuantity();

                product.setStockQuantity(newStock);

                if (newStock >= product.getMinQuantity() && prevStock < product.getMinQuantity()){
                    notificationService.resolveLowStockNotification(product.getId());
                }
            }
        }

        if (payment != null &&  payment.getMethod() != PaymentMethod.COD && payment.getStatus() == PaymentStatus.PAID) {
            events.publishEvent(new RefundRequestedEvent(payment.getRazorpayPaymentId(), payment.getAmountPaise()));
        }

        notificationService.createNotification(NotificationType.ORDER_CANCELLED , 1L , RecipientType.ADMIN , ReferenceType.ORDER , id ,
                "Order cancelled", "Order #"+id+" cancelled successfully done" , null);

        notificationService.createNotification(NotificationType.ORDER_CANCELLED , order.getUser().getId() , RecipientType.USER , ReferenceType.ORDER , id ,
                "Order cancelled", "Your Order #"+id+" cancelled By Authorities" , null);

        return  "Order cancelled Successfully";
    }



    @Transactional
    public  Page<NotificationDto> getNotifications(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        Pageable pageable = PageRequest.of(page , size, Sort.by(Sort.Direction.DESC ,    "createdAt")
                .and(Sort.by(Sort.Direction.DESC ,    "id")));

        Page<Notification> notifications = notificationRepository.findByRecipientIdAndRecipientType(1L, RecipientType.ADMIN , pageable);

        return notifications.map(this::getNotificationDto);
    }

    private NotificationDto getNotificationDto(Notification notification){
        return new NotificationDto(notification.getId(),notification.getType() , notification.getRecipientId(),  notification.getRecipientType() ,
                notification.getReferenceType(), notification.getReferenceId(), notification.getTitle() , notification.getMessage() ,
                notification.isRead() , notification.getReadAt() , notification.getCreatedAt() , notification.isResolved(),
                notification.getResolvedAt());

    }

    @Transactional
    public  Long unReadNotification() {
        return notificationRepository.countByRecipientIdAndRecipientTypeAndIsReadFalse(1L , RecipientType.ADMIN);
    }

    @Transactional
    public void markReadNotification(Long id) {
        if(id == null  ){
            throw  new IllegalArgumentException("notification id can not be null");
        }
        Notification notification = notificationRepository.findByIdAndRecipientIdAndRecipientType(id, 1L, RecipientType.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if(!notification.isRead()){
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void markReadAllNotification() {
        notificationRepository.markAllAsRead(1L , RecipientType.ADMIN , LocalDateTime.now());
    }

    @Transactional
    public void resolveNotification(Long id) {
        if(id == null ){
            throw  new IllegalArgumentException("notification id and user id can not be null");
        }
        Notification notification = notificationRepository.findByIdAndRecipientIdAndRecipientType(id,  1L , RecipientType.ADMIN).orElseThrow(()->new ResourceNotFoundException("Notification not found"));

        if(!notification.isResolved()){
            notification.setResolved(true);
            notification.setResolvedAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void resolveAllNotification() {
        notificationRepository.resolveAll(1L , RecipientType.ADMIN , LocalDateTime.now());
    }


   @Transactional
    public Product moderateReview(Long reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        if (status == null) {
            throw new BadRequestException("Review status is required");
        }

        if (status == ReviewStatus.PENDING) {
            throw new BadRequestException("Admin cannot moderate review to PENDING");
        }

        review.setStatus(status);
        reviewRepository.save(review);

        return review.getProduct();
    }

    public  ReviewResponseDto getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(()->new ResourceNotFoundException("Review not found"));
        return reviewService.mapToResponse(review);
    }


    // Admin: no 30 minute window
    @Transactional
    public String adminPartialDelete(PartialOrderDeleteDto dto) {
        Order order = orderRepository.findByIdForUpdate(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!ADMIN_PARTIAL_OK.contains(order.getStatus())) {
            throw new IllegalStateException("Items can't be cancelled in the current order status");
        }
        return orderService.cancelItems(order, dto.getOrderItemIds(), true);
    }
}
