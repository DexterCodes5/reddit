package dev.dex.reddit.service;

import dev.dex.reddit.entity.Question;
import dev.dex.reddit.models.responsemodel.AnswerResponse;
import dev.dex.reddit.models.responsemodel.QuestionAndAnswersResponse;
import dev.dex.reddit.models.responsemodel.QuestionResponse;
import dev.dex.reddit.models.responsemodel.UserResponse;
import dev.dex.reddit.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public void save(Question question) {
        questionRepository.save(question);
    }

    public List<QuestionResponse> findAll() {
        List<Question> questions = questionRepository.findAll();
        List<QuestionResponse> questionResponses = questions.stream().map(q -> new QuestionResponse(
                q.getId(), q.getTitle(), q.getContent(),
                q.getPostTimestamp(), new UserResponse(q.getUser().getId(), q.getUser().getUsername(), q.getUser().getImg())))
                .toList();
        return questionResponses;
    }

    public QuestionAndAnswersResponse findById(int id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid id"));
        List<AnswerResponse> answers = new ArrayList<>(question.getAnswers().stream()
                .map(a -> new AnswerResponse(a.getId(), a.getContent(), a.getPostTimestamp(),
                        new UserResponse(a.getUser().getId(), a.getUser().getUsername(), a.getUser().getImg())))
                .toList());
        Collections.sort(answers, (a1, a2) -> a1.getId() - a2.getId());
        return new QuestionAndAnswersResponse(question.getId(), question.getTitle(), question.getContent(),
                question.getPostTimestamp(),
                new UserResponse(question.getUser().getId(), question.getUser().getUsername(), question.getUser().getImg()),
                answers);
    }
}
