package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.*;
import ecommerce.app.Ecommerce.entity.Product;
import ecommerce.app.Ecommerce.enums.ReviewStatus;
import ecommerce.app.Ecommerce.service.AdminService;
import ecommerce.app.Ecommerce.service.DashboardService;
import ecommerce.app.Ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private AdminService adminService;
    private ReviewService reviewService;
    private DashboardService dashboardService;

    public AdminController( AdminService adminService , ReviewService reviewService , DashboardService dashboardService){
        this.adminService = adminService;
        this.reviewService = reviewService;
        this.dashboardService = dashboardService;
    }

    @PostMapping("/product/add")
    public ResponseEntity<ProductResponseDto> addProduct(@RequestBody ProductReqDto productReqDto){
        ProductResponseDto productResponseDto = adminService.addProduct(productReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDto);
    }

    @PostMapping("/category/add")
    public ResponseEntity<CategoryResponseDto> addCategory(@RequestBody CategoryReqDto categoryReqDto){
        CategoryResponseDto categoryResponseDto = adminService.addCategory(categoryReqDto);
        return  ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDto);
    }

    @DeleteMapping("/product/delete/{id}")
    public ResponseEntity<ProductResponseDto> deleteProduct(@PathVariable Long id){
        ProductResponseDto productResponseDto = adminService.deleteProduct(id);
        return ResponseEntity.ok().body(productResponseDto);
    }
    @DeleteMapping("/category/delete/{id}")
    public ResponseEntity<CategoryResponseDto> deleteCategory(@PathVariable Long id){
        CategoryResponseDto categoryResponseDto = adminService.deleteCategory(id);
        return ResponseEntity.ok().body(categoryResponseDto);
    }

    @PatchMapping("/product/update")
    public  ResponseEntity<ProductResponseDto> updateProduct(@RequestBody ProductReqDto productReqDto ) {
        ProductResponseDto productResponseDto = adminService.updateProduct(productReqDto);
        return ResponseEntity.ok().body(productResponseDto);
    }

    @GetMapping("/orders")
    public PagedResponse<AdminOrderResponseDto>  getOrders(@Valid @ModelAttribute OrderFilterRequest filter){
        return adminService.getorders(filter);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<AdminOrderDetails> getOrdersDetails(@PathVariable Long id){
        return ResponseEntity.ok(adminService.getOrderDetails(id));
    }

    @PatchMapping("order/status")
    public ResponseEntity<String> updateOrderStatus(@RequestBody AdminOrderStatusDto adminOrderStatusDto){
        return ResponseEntity.ok(adminService.updateOrderStatus(adminOrderStatusDto));
    }

    @PatchMapping("order/paymentStatus")
    public  ResponseEntity<String> updatePaymentStatus(@RequestBody AdminPaymentStatusDto adminPaymentStatusDto){
        return  ResponseEntity.ok(adminService.updatePaymentStatus(adminPaymentStatusDto));
    }

    @DeleteMapping("order/delete/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id){
        return ResponseEntity.ok(adminService.cancelOrder(id));
    }

    //Notifications Controllers
    @GetMapping("notification")
    public ResponseEntity<Page<NotificationDto>> getNotification(@RequestParam(defaultValue = "0") int page , @RequestParam(defaultValue = "10") int size ){
        return  ResponseEntity.ok(adminService.getNotifications(page , size ));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> unReadNotification(Authentication auth){
        return ResponseEntity.ok(adminService.unReadNotification());
    }

    @PatchMapping("read/{id}")
    public ResponseEntity<Void> markReadNotification(@PathVariable Long id , Authentication auth){
        adminService.markReadNotification(id );
        return ResponseEntity.ok().build();
    }

    @PatchMapping("read/all")
    public ResponseEntity<Void> markReadAllNotification(Authentication auth){
        adminService.markReadAllNotification();
        return ResponseEntity.ok().build();
    }

    @PatchMapping("resolve/{id}")
    public ResponseEntity<Void> resolveNotification(@PathVariable Long id ){
        adminService.resolveNotification(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("resolve/all")
    public ResponseEntity<Void> resolveAllNotification(Authentication auth){
        adminService.resolveAllNotification();
        return ResponseEntity.ok().build();
    }


    @PatchMapping("/moderate/{reviewId}")
    public ResponseEntity<String> moderateReview(@PathVariable Long reviewId, @RequestParam ReviewStatus status) {
        Product product =  adminService.moderateReview(reviewId, status);
        reviewService.refreshRatingSummary(product);
        return ResponseEntity.ok("Review moderated successfully");
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable("id")  Long reviewId ){
        return ResponseEntity.ok(adminService.getReview(reviewId));
    }



    @GetMapping("/overview")
    public ResponseEntity<DashboardOverviewDto> getOverview(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        if (from.isAfter(to)) {
            throw new IllegalArgumentException(
                    "From date cannot be after to date"
            );
        }

        return ResponseEntity.ok(
                dashboardService.getOverview(
                        from.atStartOfDay(),
                        to.plusDays(1).atStartOfDay()
                )
        );
    }

    @GetMapping("/monthly-sales")
    public ResponseEntity<List<MonthlySalesDto>> getMonthlySales(@RequestParam Integer year) {
        return ResponseEntity.ok(
                dashboardService.getMonthlySales(year)
        );
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDto>> getTopProducts(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(
                dashboardService.getTopProducts(
                        from.atStartOfDay(),
                        to.plusDays(1).atStartOfDay()
                )
        );
    }

    @GetMapping("/low-stock-products")
    public ResponseEntity<List<LowStockProductDto>> getLowStockProducts() {
        return ResponseEntity.ok(dashboardService.getLowStockProducts());
    }

    @PatchMapping("order/partial-delete")
    public ResponseEntity<String> partialDeleteOrder(@RequestBody PartialOrderDeleteDto partialOrderDeleteDto){
        return ResponseEntity.ok(adminService.adminPartialDelete(partialOrderDeleteDto));
    }


}
