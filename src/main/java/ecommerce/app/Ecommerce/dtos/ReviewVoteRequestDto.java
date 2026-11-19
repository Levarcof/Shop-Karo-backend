package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.enums.VoteType;
import jakarta.validation.constraints.NotNull;

public class ReviewVoteRequestDto {
    @NotNull(message = "Vote type is required")
    private VoteType voteType;
}
