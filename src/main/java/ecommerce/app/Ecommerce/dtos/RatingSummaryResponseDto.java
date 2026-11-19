package ecommerce.app.Ecommerce.dtos;

import java.math.BigDecimal;

public class RatingSummaryResponseDto {

    private Long productId;

    private BigDecimal averageRating;

    private Long totalRatings;

    private Long totalReviews;

    private Long fiveStarCount;

    private Long fourStarCount;

    private Long threeStarCount;

    private Long twoStarCount;

    private Long oneStarCount;

    public RatingSummaryResponseDto() {
    }

    public RatingSummaryResponseDto(Long productId, BigDecimal averageRating, Long totalRatings, Long totalReviews, Long fiveStarCount, Long fourStarCount, Long threeStarCount, Long twoStarCount, Long oneStarCount) {
        this.productId = productId;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
        this.totalReviews = totalReviews;
        this.fiveStarCount = fiveStarCount;
        this.fourStarCount = fourStarCount;
        this.threeStarCount = threeStarCount;
        this.twoStarCount = twoStarCount;
        this.oneStarCount = oneStarCount;
    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
}
