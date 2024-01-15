package dev.dex.reddit.service;

import dev.dex.reddit.entity.QuestionRating;
import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.repository.QuestionRatingRepository;
import dev.dex.reddit.repository.QuestionRepository;
import dev.dex.reddit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class QuestionRatingServiceTest {
    @Mock
    private QuestionRatingRepository questionRatingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private QuestionRepository questionRepository;
    private QuestionRatingService underTest;

    @BeforeEach
    void setUp() {
        underTest = new QuestionRatingService(questionRatingRepository, userRepository, questionRepository);
    }

    @Test
    void canGetRating() {
        // given
        int questionId = 1;

        given(questionRatingRepository.countByQuestionIdAndUpvote(questionId, true))
                .willReturn(10L);
        given(questionRatingRepository.countByQuestionIdAndDownvote(questionId, true))
                .willReturn(5L);

        // when
        int res = underTest.getRating(questionId);

        // then
        assertThat(res).isEqualTo(5);
    }

    @Test
    void canRate() {
        // given
        QuestionRating questionRating = new QuestionRating(null, true, false, 1, 1);

        given(userRepository.existsById(anyInt()))
                .willReturn(true);
        given(questionRepository.existsById(anyInt()))
                .willReturn(true);

        QuestionRating questionRating2 = new QuestionRating(1, true, false, 1, 1);
        given(questionRatingRepository.findByUserIdAndQuestionId(anyInt(), anyInt()))
                .willReturn(Optional.of(questionRating2));

        // when
        underTest.rate(questionRating);

        // then
        verify(questionRatingRepository)
                .save(questionRating2);
    }

    @Test
    void willThrowInRateWhenUserIdInvalid() {
        // given
        QuestionRating questionRating = new QuestionRating(null, true, false, 1, 1);

        // when
        // then
        assertThatThrownBy(() -> underTest.rate(questionRating))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid user id and question id");
    }

    @Test
    void willThrowInRateWhenUpvoteAndDownvote() {
        // given
        QuestionRating questionRating = new QuestionRating(null, true, true, 1, 1);

        given(userRepository.existsById(anyInt()))
                .willReturn(true);
        given(questionRepository.existsById(anyInt()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.rate(questionRating))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Upvote and downvote cannot be true");
    }

    @Test
    void canGetUserQuestionRating() {
        // given
        int questionId = 1;
        User user = new User(1, "dexter", "test123", true,
                null, "dexter@mail.com", Role.USER, null, null, null,
                null);
        Principal principal = new UsernamePasswordAuthenticationToken(user, null);

        // when
        underTest.getUserQuestionRating(questionId, principal);

        // then
        verify(questionRatingRepository).findByUserIdAndQuestionId(user.getId(), questionId);
    }
}