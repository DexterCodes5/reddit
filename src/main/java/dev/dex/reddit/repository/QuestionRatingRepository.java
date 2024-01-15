package dev.dex.reddit.repository;

import dev.dex.reddit.entity.QuestionRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRatingRepository extends JpaRepository<QuestionRating, Integer> {
    long countByQuestionIdAndUpvote(Integer questionId, Boolean upvote);

    long countByQuestionIdAndDownvote(Integer questionId, boolean downvote);

    Optional<QuestionRating> findByUserIdAndQuestionId(int userId, int questionId);
}
