package dev.dex.reddit.service;

import dev.dex.reddit.entity.AnswerRating;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.repository.AnswerRatingRepository;
import dev.dex.reddit.repository.AnswerRepository;
import dev.dex.reddit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerRatingService {
    private final AnswerRatingRepository answerRatingRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    public int getRating(int answerId) {
        int upvotes = answerRatingRepository.countByAnswerIdAndUpvote(answerId, true);
        int downvotes = answerRatingRepository.countByAnswerIdAndDownvote(answerId, true);
        return upvotes - downvotes;
    }

    public AnswerRating getUserAnswerRating(int answerId, Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        return answerRatingRepository.findByAnswerIdAndUserId(answerId, user.getId())
                .orElse(null);
    }

    public void rate(AnswerRating answerRating) {
        if (!userRepository.existsById(answerRating.getUserId())
                || !answerRepository.existsById(answerRating.getAnswerId())) {
            throw new RuntimeException("Invalid user id and answer id");
        }
        if (answerRating.isUpvote() == true && answerRating.isDownvote() == true) {
            throw new RuntimeException("Upvote and downvote cannot be true");
        }
        Optional<AnswerRating> answerRatingFromDb = answerRatingRepository.findByAnswerIdAndUserId(answerRating.getAnswerId(),
                answerRating.getUserId());
        if (answerRatingFromDb.isPresent()) {
            answerRating.setId(answerRatingFromDb.get().getId());
        }
        answerRatingRepository.save(answerRating);
    }
}
