package dev.dex.reddit.service;

import dev.dex.reddit.entity.Question;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.requestmodels.QuestionRequest;
import dev.dex.reddit.models.responsemodels.MyQuestionResponse;
import dev.dex.reddit.models.responsemodels.QuestionResponse;
import dev.dex.reddit.models.responsemodels.UserResponse;
import dev.dex.reddit.repository.AnswerRepository;
import dev.dex.reddit.repository.QuestionRepository;
import dev.dex.reddit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuestionRatingService questionRatingService;
    private final AnswerRepository answerRepository;

    public void save(QuestionRequest questionRequest) {
        User user = userRepository.findById(questionRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("Invalid user id"));
        Question question = Question.builder()
                .title(questionRequest.getTitle())
                .content(questionRequest.getContent())
                .postTimestamp(new Timestamp(System.currentTimeMillis()))
                .user(user)
                .build();
        questionRepository.save(question);
    }

    public Page<QuestionResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Question> questions = questionRepository.findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + size), questions.size());
        List<Question> questionsForPage = questions.subList(start, end);

        List<QuestionResponse> questionResponses = questionsForPage.stream()
                .map(q ->
                        QuestionResponse.builder()
                                .id(q.getId())
                                .title(q.getTitle())
                                .content(q.getContent())
                                .postTimestamp(q.getPostTimestamp())
                                .rating(questionRatingService.getRating(q.getId()))
                                .user(UserResponse.builder()
                                        .id(q.getUser().getId())
                                        .username(q.getUser().getUsername())
                                        .img(q.getUser().getImg())
                                        .build())
                                .build()
                ).toList();
        return new PageImpl<>(questionResponses, pageable, questions.size());
    }

    public QuestionResponse findById(int id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid id"));
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .postTimestamp(question.getPostTimestamp())
                .rating(questionRatingService.getRating(question.getId()))
                .user(UserResponse.builder()
                        .id(question.getUser().getId())
                        .username(question.getUser().getUsername())
                        .img(question.getUser().getImg())
                        .build())
                .build();
    }

    public List<MyQuestionResponse> findByUser(String username) {
        List<Question> questions = questionRepository.findByUserUsername(username);
        return questions.stream().map(q -> new MyQuestionResponse(q.getId(), q.getTitle(), questionRatingService.getRating(q.getId()),
                answerRepository.countByQuestionId(q.getId())))
                .toList();
    }
}
