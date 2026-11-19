package ecommerce.app.Ecommerce.entity;

import jakarta.persistence.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class ProductRatingSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One summary per product
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_id",
            nullable = false,
            unique = true
    )
    private Product product;

    // Average rating
    @Column(
            precision = 3,
            scale = 2,
            nullable = false
    )
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    // Total approved ratings
    @Column(nullable = false)
    @Builder.Default
    private Long totalRatings = 0L;

    // Total approved textual reviews
    @Column(nullable = false)
    @Builder.Default
    private Long totalReviews = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long fiveStarCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long fourStarCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long threeStarCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long twoStarCount = 0L;

    @Column(nullable = false)
    @Builder.Default
    private Long oneStarCount = 0L;

    private LocalDateTime updatedAt;





    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }

    public Long getFiveStarCount() {
        return fiveStarCount;
    }

    public void setFiveStarCount(Long fiveStarCount) {
        this.fiveStarCount = fiveStarCount;
    }

    public Long getFourStarCount() {
        return fourStarCount;
    }

    public void setFourStarCount(Long fourStarCount) {
        this.fourStarCount = fourStarCount;
    }

    public Long getThreeStarCount() {
        return threeStarCount;
    }

    public void setThreeStarCount(Long threeStarCount) {
        this.threeStarCount = threeStarCount;
    }

    public Long getTwoStarCount() {
        return twoStarCount;
    }

    public void setTwoStarCount(Long twoStarCount) {
        this.twoStarCount = twoStarCount;
    }

    public Long getOneStarCount() {
        return oneStarCount;
    }

    public void setOneStarCount(Long oneStarCount) {
        this.oneStarCount = oneStarCount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
