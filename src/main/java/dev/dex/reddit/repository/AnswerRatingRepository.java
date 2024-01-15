package dev.dex.reddit.repository;

import dev.dex.reddit.entity.AnswerRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRatingRepository extends JpaRepository<AnswerRating, Integer> {
    Optional<AnswerRating> findByAnswerIdAndUserId(int answerId, int userId);
    int countByAnswerIdAndUpvote(int answerId, boolean upvote);
    int countByAnswerIdAndDownvote(int answerId, boolean downvote);
}
