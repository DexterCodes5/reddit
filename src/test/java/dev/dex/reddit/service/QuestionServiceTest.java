package dev.dex.reddit.service;

import dev.dex.reddit.entity.Question;
import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.requestmodels.QuestionRequest;
import dev.dex.reddit.models.responsemodels.MyQuestionResponse;
import dev.dex.reddit.models.responsemodels.QuestionResponse;
import dev.dex.reddit.models.responsemodels.UserResponse;
import dev.dex.reddit.repository.AnswerRepository;
import dev.dex.reddit.repository.QuestionRepository;
import dev.dex.reddit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private QuestionRatingService questionRatingService;
    @Mock
    private AnswerRepository answerRepository;
    private QuestionService underTest;

    @BeforeEach
    void setUp() {
        underTest = new QuestionService(questionRepository, userRepository, questionRatingService, answerRepository);
    }

    @Test
    void canSave() {
        // given
        QuestionRequest questionRequest = new QuestionRequest("Title", "content", 1);

        User user = new User();
        given(userRepository.findById(anyInt()))
                .willReturn(Optional.of(user));

        // when
        underTest.save(questionRequest);

        // then
        ArgumentCaptor<Question> questionArgumentCaptor = ArgumentCaptor.forClass(Question.class);
        verify(questionRepository).save(questionArgumentCaptor.capture());
        Question capturedQuestion = questionArgumentCaptor.getValue();
        assertThat(capturedQuestion.getTitle()).isEqualTo(questionRequest.getTitle());
        assertThat(capturedQuestion.getContent()).isEqualTo(questionRequest.getContent());
        assertThat(capturedQuestion.getUser()).isEqualTo(user);
    }

    @Test
    void canFindAll() {
        // given
        int page = 0;
        int size = 1;

        User user = new User(1, "dexter", "test123", true,
                null, "dexter@mail.com", Role.USER, null, null, null,
                null);
        Question question = new Question(0, "title", "content", new Timestamp(System.currentTimeMillis()), user);
        List<Question> questions = List.of(question);
        given(questionRepository.findAll())
                .willReturn(questions);

        int rating = 10;
        given(questionRatingService.getRating(anyInt()))
                .willReturn(rating);

        // when
        Page<QuestionResponse> res = underTest.findAll(page, size);

        // then
        List<QuestionResponse> resList = res.stream().toList();
        assertThat(resList.get(0)).isEqualTo(
                QuestionResponse.builder()
                        .id(question.getId())
                        .title(question.getTitle())
                        .content(question.getContent())
                        .postTimestamp(question.getPostTimestamp())
                        .rating(rating)
                        .user(UserResponse.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .img(user.getImg())
                                .build())
                        .build()
        );
    }

    @Test
    void canFindById() {
        // given
        int id = 1;

        User user = new User(1, "dexter", "test123", true,
                null, "dexter@mail.com", Role.USER, null, null, null,
                null);
        Question question = new Question(0, "title", "content", new Timestamp(System.currentTimeMillis()), user);
        given(questionRepository.findById(anyInt()))
                .willReturn(Optional.of(question));

        int rating = 10;
        given(questionRatingService.getRating(anyInt()))
                .willReturn(rating);

        // when
        QuestionResponse res = underTest.findById(id);

        // then
        assertThat(res).isEqualTo(
                QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .postTimestamp(question.getPostTimestamp())
                .rating(rating)
                .user(UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .img(user.getImg())
                        .build())
                .build()
        );
    }

    @Test
    void canFindByUser() {
        // given
        String username = "dexter";

        User user = new User(1, "dexter", "test123", true,
                null, "dexter@mail.com", Role.USER, null, null, null,
                null);
        int questionId = 1;
        Question question = new Question(questionId, "title", "content", new Timestamp(System.currentTimeMillis()), user);
        List<Question> questions = List.of(question);
        given(questionRepository.findByUserUsername(anyString()))
                .willReturn(questions);

        int rating = 10;
        given(questionRatingService.getRating(anyInt()))
                .willReturn(rating);

        int answers = 10;
        given(answerRepository.countByQuestionId(anyInt()))
                .willReturn(answers);

        // when
        List<MyQuestionResponse> res = underTest.findByUser(username);

        // then
        assertThat(res.get(0)).isEqualTo(new MyQuestionResponse(question.getId(), question.getTitle(), rating, answers));
    }
}