package ecommerce.app.Ecommerce.entity;

import ecommerce.app.Ecommerce.enums.ReviewStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "reviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_review_order_item",
                        columnNames = "order_item_id"
                )
        },
        indexes = {
                @Index(name = "idx_review_product", columnList = "product_id"),
                @Index(name = "idx_review_user", columnList = "user_id"),
                @Index(name = "idx_review_status", columnList = "status"),
                @Index(name = "idx_review_created", columnList = "created_at")
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Customer who submitted review
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Reviewed product
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Purchase reference
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    // Rating between 1 and 5
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(nullable = false)
    private Integer rating = 0;

    // Review heading
    @Column(length = 150)
    private String title;

    // Review description
    @Column(columnDefinition = "TEXT")
    private String comment;

    // Verified purchase indicator
    @Column(nullable = false)
    @Builder.Default
    private Boolean verifiedPurchase = true;

    // Moderation status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.PENDING;

    // Review images
    @OneToMany(
            mappedBy = "review",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ReviewImage> images = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;






    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public @Min(value = 1, message = "Rating must be at least 1") @Max(value = 5, message = "Rating cannot exceed 5") Integer getRating() {
        return rating;
    }

    public void setRating(@Min(value = 1, message = "Rating must be at least 1") @Max(value = 5, message = "Rating cannot exceed 5") Integer rating) {
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

    public List<ReviewImage> getImages() {
        return images;
    }

    public void setImages(List<ReviewImage> images) {
        this.images = images;
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
