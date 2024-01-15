package dev.dex.reddit.service;

import dev.dex.reddit.entity.QuestionRating;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.repository.QuestionRatingRepository;
import dev.dex.reddit.repository.QuestionRepository;
import dev.dex.reddit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuestionRatingService {
    private final QuestionRatingRepository questionRatingRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    public Integer getRating(Integer questionId) {
        long upvotes = questionRatingRepository.countByQuestionIdAndUpvote(questionId, true);
        long downvotes = questionRatingRepository.countByQuestionIdAndDownvote(questionId, true);
        return (int) (upvotes - downvotes);
    }

    public void rate(QuestionRating questionRating) {
        if (!userRepository.existsById(questionRating.getUserId())
                || !questionRepository.existsById(questionRating.getQuestionId())) {
            throw new RuntimeException("Invalid user id and question id");
        }
        if (questionRating.getUpvote() == true && questionRating.getDownvote() == true) {
            throw new RuntimeException("Upvote and downvote cannot be true");
        }
        Optional<QuestionRating> questionRatingFromDb = questionRatingRepository.findByUserIdAndQuestionId(
                questionRating.getUserId(), questionRating.getQuestionId());
        if (questionRatingFromDb.isPresent()) {
            questionRating.setId(questionRatingFromDb.get().getId());
        }
        questionRatingRepository.save(questionRating);
    }

    public QuestionRating getUserQuestionRating(int questionId, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return questionRatingRepository.findByUserIdAndQuestionId(user.getId(), questionId)
                .orElse(null);
    }
}
