package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.ReviewStatus;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponseDto {


    private Long id;

    private Long userId;

    private String customerName;

    private Long productId;

    private Integer rating;

    private String title;

    private String comment;

    private Boolean verifiedPurchase;

    private ReviewStatus status;

    private List<String> imageUrls;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    public ReviewResponseDto( Long id ,Long userId, String customerName, Long productId, Integer rating, String title, String comment, Boolean verifiedPurchase, ReviewStatus status, List<String> imageUrls, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.customerName = customerName;
        this.productId = productId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
        this.verifiedPurchase = verifiedPurchase;
        this.status = status;
        this.imageUrls = imageUrls;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public void setStatus(ReviewStatus status) {
        this.status = status;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
