package dev.dex.reddit.controller;

import dev.dex.reddit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ITUtils {
    private final UserRepository userRepository;
    private final AnswerRatingRepository answerRatingRepository;
    private final AnswerRepository answerRepository;
    private final ImageDataRepository imageDataRepository;
    private final QuestionRatingRepository questionRatingRepository;
    private final QuestionRepository questionRepository;

    void deleteAll() {
        answerRatingRepository.deleteAll();
        answerRepository.deleteAll();
        questionRatingRepository.deleteAll();
        questionRepository.deleteAll();
        userRepository.deleteAll();
        imageDataRepository.deleteAll();
    }
}
