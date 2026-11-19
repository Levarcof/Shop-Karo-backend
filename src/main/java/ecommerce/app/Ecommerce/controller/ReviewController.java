package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.RatingSummaryResponseDto;
import ecommerce.app.Ecommerce.dtos.ReviewRequestDto;
import ecommerce.app.Ecommerce.dtos.ReviewResponseDto;
import ecommerce.app.Ecommerce.dtos.ReviewUpdateRequest;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.UserRepository;
import ecommerce.app.Ecommerce.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    private ReviewService reviewService;
    private UserRepository userRepository;

    public ReviewController(ReviewService reviewService, UserRepository userRepository) {
        this.reviewService = reviewService;
        this.userRepository = userRepository;
    }

    private Long currentUserId(Authentication auth){
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String email = jwtAuth.getToken().getSubject();

            return userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found")
                    )
                    .getId();
        }

        throw new IllegalStateException("JWT authentication required");
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(@Valid  @RequestBody ReviewRequestDto requestDto , Authentication auth ){
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(requestDto , currentUserId(auth)));
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(Authentication auth, @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request) {

        ReviewResponseDto response = reviewService.updateReview(currentUserId(auth), reviewId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(Authentication auth, @PathVariable Long reviewId) {

        reviewService.deleteReview(currentUserId(auth), reviewId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{productId}")
    public ResponseEntity<Page<ReviewResponseDto>> getProductReviews(@PathVariable Long productId, @RequestParam(defaultValue = "0")
            int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "newest") String sort) {

        Page<ReviewResponseDto> response = reviewService.getProductReviews(productId,  page ,  size , sort);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rating-summary/{productId}")
    public ResponseEntity<RatingSummaryResponseDto> getRatingSummary(@PathVariable Long productId) {

        RatingSummaryResponseDto response = reviewService.getRatingSummary(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/review/{id}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable("id")  Long orderItemId , Authentication auth){
        return ResponseEntity.ok(reviewService.getReview(orderItemId , currentUserId(auth)));
    }

}
