package ecommerce.app.Ecommerce.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewUpdateRequest {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @Size(max = 150)
    private String title;

    @Size(max = 5000)
    private String comment;




    public @NotNull @Min(1) @Max(5) Integer getRating() {
        return rating;
    }

    public void setRating(@NotNull @Min(1) @Max(5) Integer rating) {
        this.rating = rating;
    }

    public @Size(max = 150) String getTitle() {
        return title;
    }

    public void setTitle(@Size(max = 150) String title) {
        this.title = title;
    }

    public @Size(max = 5000) String getComment() {
        return comment;
    }

    public void setComment(@Size(max = 5000) String comment) {
        this.comment = comment;
    }
}
