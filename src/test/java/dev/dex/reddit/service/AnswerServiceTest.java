package dev.dex.reddit.service;

import dev.dex.reddit.entity.Answer;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.requestmodels.AnswerRequest;
import dev.dex.reddit.models.responsemodels.AnswerResponse;
import dev.dex.reddit.models.responsemodels.UserResponse;
import dev.dex.reddit.repository.AnswerRepository;
import dev.dex.reddit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AnswerRatingService answerRatingService;
    private AnswerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AnswerService(answerRepository, userRepository, answerRatingService);
    }

    @Test
    void canSave() {
        // given
        AnswerRequest answerRequest = new AnswerRequest(1, 1, "This is my answer");

        User user = new User();
        given(userRepository.findById(anyInt()))
                .willReturn(Optional.of(user));

        // when
        underTest.save(answerRequest);

        // then
        verify(userRepository)
                .findById(1);
        ArgumentCaptor<Answer> answerArgumentCaptor = ArgumentCaptor.forClass(Answer.class);
        verify(answerRepository)
                .save(answerArgumentCaptor.capture());
        Answer capturedAnswer = answerArgumentCaptor.getValue();
        assertThat(capturedAnswer.getContent()).isEqualTo(answerRequest.getAnswer());
        assertThat(capturedAnswer.getQuestionId()).isEqualTo(answerRequest.getQuestionId());
        assertThat(capturedAnswer.getUser()).isEqualTo(user);
    }

    @Test
    void willThrowInSaveWhenUserIdInvalid() {
        // given
        AnswerRequest answerRequest = new AnswerRequest(1, 1, "This is my answer");

        // when
        // then
        assertThatThrownBy(() -> underTest.save(answerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid user id");
    }


    @Test
    void canFindByQuestionId() {
        // given
        int questionId = 1;

        User user = new User();
        user.setId(1);
        Answer answer = new Answer(1, "content", new Timestamp(System.currentTimeMillis()), questionId, user);
        List<Answer> answers = List.of(answer);
        given(answerRepository.findByQuestionId(anyInt()))
                .willReturn(answers);

        int rating = 10;
        given(answerRatingService.getRating(anyInt()))
                .willReturn(rating);

        // when
        List<AnswerResponse> res = underTest.findByQuestionId(questionId);

        // then
        verify(answerRepository).findByQuestionId(questionId);

        assertThat(res.get(0)).isEqualTo(new AnswerResponse(answer.getId(), answer.getContent(), answer.getPostTimestamp(),
                new UserResponse(1, null, null), rating));
    }
}