package ecommerce.app.Ecommerce.entity;

import ecommerce.app.Ecommerce.enums.VoteType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ReviewHelpfulVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VoteType voteType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
