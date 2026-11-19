package ecommerce.app.Ecommerce.repository;

public interface RatingAggregateProjection {

    Long getTotalRatings();

    Double getAverageRating();

    Long getTotalReviews();

    Long getFiveStarCount();

    Long getFourStarCount();

    Long getThreeStarCount();

    Long getTwoStarCount();

    Long getOneStarCount();
}
