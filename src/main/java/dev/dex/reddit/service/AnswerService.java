package dev.dex.reddit.service;

import dev.dex.reddit.entity.Answer;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.requestmodels.AnswerRequest;
import dev.dex.reddit.models.responsemodels.AnswerResponse;
import dev.dex.reddit.models.responsemodels.UserResponse;
import dev.dex.reddit.repository.AnswerRepository;
import dev.dex.reddit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final AnswerRatingService answerRatingService;

    public void save(AnswerRequest answerRequest) {
        User user = userRepository.findById(answerRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Invalid user id"));
        answerRepository.save(Answer.builder()
                        .content(answerRequest.getAnswer())
                        .postTimestamp(new Timestamp(System.currentTimeMillis()))
                        .questionId(answerRequest.getQuestionId())
                        .user(user)
                .build());
    }

    public List<AnswerResponse> findByQuestionId(int questionId) {
        List<Answer> answers = answerRepository.findByQuestionId(questionId);
        return answers.stream()
                .map(answer -> AnswerResponse.builder()
                        .id(answer.getId())
                        .content(answer.getContent())
                        .postTimestamp(answer.getPostTimestamp())
                        .user(UserResponse.builder()
                                .id(answer.getUser().getId())
                                .username(answer.getUser().getUsername())
                                .img(answer.getUser().getImg())
                                .build())
                        .rating(answerRatingService.getRating(answer.getId()))
                        .build())
                .toList();
    }
}
