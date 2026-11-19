package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.RatingSummaryResponseDto;
import ecommerce.app.Ecommerce.dtos.ReviewRequestDto;
import ecommerce.app.Ecommerce.dtos.ReviewResponseDto;
import ecommerce.app.Ecommerce.dtos.ReviewUpdateRequest;
import ecommerce.app.Ecommerce.entity.*;
import ecommerce.app.Ecommerce.enums.*;
import ecommerce.app.Ecommerce.exception.BadRequestException;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.*;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    private ReviewRepository reviewRepository;
    private ProductRepository productRepository;
    private OrderItemRepository orderItemRepository;
    private ReviewImageRepository reviewImageRepository;
    private NotificationService notificationService;
    private ProductRatingSummaryRepository productRatingSummaryRepository;
    public ReviewService(ProductRatingSummaryRepository productRatingSummaryRepository , ReviewRepository reviewRepository, ProductRepository productRepository, OrderItemRepository orderItemRepository, ReviewImageRepository reviewImageRepository, NotificationService notificationService) {
        this.productRatingSummaryRepository = productRatingSummaryRepository;
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.reviewImageRepository = reviewImageRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public  ReviewResponseDto createReview(ReviewRequestDto request, Long userId) {
        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        OrderItem orderItem = orderItemRepository.findById(request.getOrderItemId()).orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        if (!orderItem.getOrder().getUser().getId().equals(userId)) {
            throw new BadRequestException("You cannot review another user's purchase");
        }

        if (!orderItem.getProduct().getId().equals(product.getId())) {
            throw new BadRequestException("Product does not match purchased item");
        }

        if (orderItem.getOrder().getStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("You can review only delivered products");
        }

        if (reviewRepository.existsByOrderItemId(orderItem.getId())) {
            throw new BadRequestException("Review already submitted for this purchase");
        }


        Review review = new Review();
        review.setUser(orderItem.getOrder().getUser());
        review.setProduct(orderItem.getProduct());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setTitle(request.getTitle());
        review.setOrderItem(orderItem);
        review.setVerifiedPurchase(true);
        review.setStatus(ReviewStatus.PENDING);

        if(request.getImageUrls() != null){
            request.getImageUrls().forEach(url->{
                ReviewImage reviewImage = new ReviewImage(review , url , LocalDateTime.now());
                review.getImages().add(reviewImage);
            });
        }

        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        Review savedReview =   reviewRepository.save(review);

        Optional<ProductRatingSummary> productRatingSummaryExist = productRatingSummaryRepository.findByProductId(product.getId());
        if(productRatingSummaryExist.isEmpty()){
            ProductRatingSummary productRatingSummary = new ProductRatingSummary();
            productRatingSummary.setProduct(product);
            productRatingSummary.setUpdatedAt(LocalDateTime.now());

            productRatingSummaryRepository.save(productRatingSummary);
        }

        notificationService.createNotification(NotificationType.REVIEW_CREATED , 1L , RecipientType.ADMIN , ReferenceType.REVIEW , savedReview.getId(),
                "Review Created" , "User "+orderItem.getOrder().getUser().getName() +" send review for product #" + product.getId()+" "+ product.getName() +" Approved This Review", null);

        return mapToResponse(savedReview);
    }

    public ReviewResponseDto mapToResponse(Review review){
        List<String> images = new ArrayList<>();
        review.getImages().forEach(reviewImage -> {
            images.add(reviewImage.getImageUrl());
        });
        return  new ReviewResponseDto(review.getId(), review.getUser().getId() , review.getUser().getName() ,  review.getProduct().getId() ,
                review.getRating() , review.getTitle() , review.getComment() , review.getVerifiedPurchase() , review.getStatus(),
                images , review.getCreatedAt(),review.getUpdatedAt());

    }

    @Transactional
    public ReviewResponseDto updateReview(Long userId, Long reviewId,  ReviewUpdateRequest request) {
        Review review = reviewRepository.findByIdAndUserId(reviewId , userId).orElseThrow(()->new ResourceNotFoundException("Review not found or access denied"));

        boolean wasApproved = review.getStatus() == ReviewStatus.APPROVED;

        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());
        review.setStatus(ReviewStatus.PENDING);

        Review updatedReview = reviewRepository.save(review);


        if (wasApproved) {
            refreshRatingSummary(review.getProduct());
        }

        notificationService.createNotification(NotificationType.REVIEW_UPDATED , 1L , RecipientType.ADMIN , ReferenceType.REVIEW , reviewId,
                "Review Created" , "Review " +reviewId+" Updated Approved This Review", null);


      return mapToResponse(updatedReview);
    }


    @Transactional
    public void refreshRatingSummary(Product product){
        ProductRatingSummary summary = productRatingSummaryRepository.findByProductIdForUpdate(product.getId()).orElseThrow(() -> new IllegalStateException("Rating summary not initialized " + "for product " + product.getId()));

        RatingAggregateProjection stats = reviewRepository.getRatingStatistics(product.getId() , ReviewStatus.APPROVED);

        summary.setTotalRatings(stats.getTotalRatings());
        summary.setTotalReviews(stats.getTotalReviews());

        summary.setAverageRating(stats.getAverageRating() == null ? BigDecimal.ZERO : BigDecimal.valueOf(stats.getAverageRating()).setScale(2, RoundingMode.HALF_UP));

        summary.setFiveStarCount(
                stats.getFiveStarCount() != null
                        ? stats.getFiveStarCount() : 0L
        );

        summary.setFourStarCount(
                stats.getFourStarCount() != null
                        ? stats.getFourStarCount() : 0L
        );

        summary.setThreeStarCount(
                stats.getThreeStarCount() != null
                        ? stats.getThreeStarCount() : 0L
        );

        summary.setTwoStarCount(
                stats.getTwoStarCount() != null
                        ? stats.getTwoStarCount() : 0L
        );

        summary.setOneStarCount(
                stats.getOneStarCount() != null
                        ? stats.getOneStarCount() : 0L
        );


        productRatingSummaryRepository.save(summary);
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        Review review = reviewRepository.findByIdAndUserId(reviewId , userId).orElseThrow(()->new ResourceNotFoundException("Review not found or access denied"));
        Product product = review.getProduct();

        boolean wasApproved = review.getStatus() == ReviewStatus.APPROVED;
        reviewRepository.delete(review);

        if (wasApproved) {
            refreshRatingSummary(product);
        }
    }

    @Transactional
    public Page<ReviewResponseDto> getProductReviews(Long productId, int page, int size , String sort) {
        Sort sorting = switch (sort.toLowerCase()) {

            case "oldest" ->
                    Sort.by("createdAt").ascending();

            case "highest" ->
                    Sort.by("rating").descending()
                            .and(Sort.by("createdAt").descending());

            case "lowest" ->
                    Sort.by("rating").ascending()
                            .and(Sort.by("createdAt").descending());

            case "newest" ->
                    Sort.by("createdAt").descending();

            default ->
                    throw new IllegalArgumentException("Invalid sort option");
        };
        Pageable pageable = PageRequest.of(
                page,
                size,
                sorting
        );

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product not found"
            );
        }

        Page<Review> reviews = reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable);

        return reviews.map(this::mapToResponse);
    }

    @Transactional
    public RatingSummaryResponseDto getRatingSummary(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product not found"
            );
        }

        ProductRatingSummary summary = productRatingSummaryRepository.findByProductId(productId).orElse(null);

        if(summary == null){
            return   new RatingSummaryResponseDto(productId , BigDecimal.ZERO , 0l,0l,0l,0l,
                    0l,0l,0l);
        }

        return mapToSummaryResponse(summary);
    }

    private  RatingSummaryResponseDto mapToSummaryResponse(ProductRatingSummary summary){
        RatingSummaryResponseDto ratingSummaryResponseDto = new RatingSummaryResponseDto(summary.getProduct().getId() , summary.getAverageRating() , summary.getTotalRatings() ,
                summary.getTotalReviews() , summary.getFiveStarCount() , summary.getFourStarCount() , summary.getThreeStarCount() ,
                summary.getTwoStarCount(), summary.getOneStarCount() );


        return ratingSummaryResponseDto;


    }

    public  ReviewResponseDto getReview(Long orderItemId, Long userId) {
        Review review = reviewRepository.findByOrderItem_IdAndUserId(orderItemId ,userId).orElseThrow(()->new ResourceNotFoundException("review not found"));
        return mapToResponse(review);
    }
}
