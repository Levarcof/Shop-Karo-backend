package ecommerce.app.Ecommerce.repository;

import ecommerce.app.Ecommerce.entity.Review;
import ecommerce.app.Ecommerce.enums.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review , Long> {

    boolean existsByOrderItemId(Long id);

    Optional<Review> findByIdAndUserId(Long reviewId, Long userId);

    Page<Review> findByProductIdAndStatus(
            Long productId,
            ReviewStatus status,
            Pageable pageable
    );


    @Query("""
    SELECT
        COUNT(r.id) AS totalRatings,

        AVG(r.rating) AS averageRating,

        COUNT(
            CASE
                WHEN r.comment IS NOT NULL
                AND TRIM(r.comment) <> ''
                THEN r.id
            END
        ) AS totalReviews,

        SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END) AS fiveStarCount,

        SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END) AS fourStarCount,

        SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END) AS threeStarCount,

        SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END) AS twoStarCount,

        SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END) AS oneStarCount

    FROM Review r

    WHERE r.product.id = :productId
    AND r.status = :status
""")
    RatingAggregateProjection getRatingStatistics(
            @Param("productId") Long productId,
            @Param("status") ReviewStatus status
    );

    Optional<Review> findByOrderItem_IdAndUserId(Long orderItemId, Long userId);
}
