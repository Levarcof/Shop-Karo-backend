package ecommerce.app.Ecommerce.dtos;

import jakarta.validation.constraints.*;

import java.util.List;

public class ReviewRequestDto {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Order item ID is required")
    private Long orderItemId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Minimum rating is 1")
    @Max(value = 5, message = "Maximum rating is 5")
    private Integer rating;

    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    @Size(max = 5000, message = "Review cannot exceed 5000 characters")
    private String comment;

    @Size(max = 5, message = "Maximum 5 images allowed")
    private List<@NotBlank @Size(max = 1000) String> imageUrls;





    public @NotNull(message = "Product ID is required") Long getProductId() {
        return productId;
    }

    public void setProductId(@NotNull(message = "Product ID is required") Long productId) {
        this.productId = productId;
    }

    public @NotNull(message = "Order item ID is required") Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(@NotNull(message = "Order item ID is required") Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public @NotNull(message = "Rating is required") @Min(value = 1, message = "Minimum rating is 1") @Max(value = 5, message = "Maximum rating is 5") Integer getRating() {
        return rating;
    }

    public void setRating(@NotNull(message = "Rating is required") @Min(value = 1, message = "Minimum rating is 1") @Max(value = 5, message = "Maximum rating is 5") Integer rating) {
        this.rating = rating;
    }

    public @Size(max = 150, message = "Title cannot exceed 150 characters") String getTitle() {
        return title;
    }

    public void setTitle(@Size(max = 150, message = "Title cannot exceed 150 characters") String title) {
        this.title = title;
    }

    public @Size(max = 5000, message = "Review cannot exceed 5000 characters") String getComment() {
        return comment;
    }

    public void setComment(@Size(max = 5000, message = "Review cannot exceed 5000 characters") String comment) {
        this.comment = comment;
    }

    public @Size(max = 5, message = "Maximum 5 images allowed") List<@NotBlank @Size(max = 1000) String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(@Size(max = 5, message = "Maximum 5 images allowed") List<@NotBlank @Size(max = 1000) String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
