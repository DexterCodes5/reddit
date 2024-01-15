package dev.dex.reddit.service;

import dev.dex.reddit.entity.AnswerRating;
import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.repository.AnswerRatingRepository;
import dev.dex.reddit.repository.AnswerRepository;
import dev.dex.reddit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnswerRatingServiceTest {
    @Mock
    private AnswerRatingRepository answerRatingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AnswerRepository answerRepository;
    private AnswerRatingService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AnswerRatingService(answerRatingRepository, userRepository, answerRepository);
    }

    @Test
    void canGetRating() {
        // given
        given(answerRatingRepository.countByAnswerIdAndUpvote(anyInt(), anyBoolean()))
                .willReturn(5);
        given(answerRatingRepository.countByAnswerIdAndDownvote(anyInt(), anyBoolean()))
                .willReturn(2);

        // when
        int res = underTest.getRating(1);

        // then
        assertThat(res).isEqualTo(3);
    }

    @Test
    void canGetUserAnswerRating() {
        // given
        User user = new User(1, "dexter", "$2a$12$V9X1Wv0cRihtUmaRkD7oEeV6iGS3e8GGw/yBOzUSFek2i9bXiSUZm", true,
                null, "dexter@mail.com", Role.USER, null, null, null,
                null);
        Principal principal = new UsernamePasswordAuthenticationToken(user, null);

        AnswerRating answerRating = new AnswerRating();
        given(answerRatingRepository.findByAnswerIdAndUserId(anyInt(), anyInt()))
                .willReturn(Optional.of(answerRating));

        // when
        AnswerRating res = underTest.getUserAnswerRating(1, principal);

        // then
        assertThat(res).isEqualTo(answerRating);
    }

    @Test
    void canRate() {
        // given
        AnswerRating answerRating = new AnswerRating();
        answerRating.setAnswerId(1);
        answerRating.setUpvote(true);
        answerRating.setDownvote(false);
        answerRating.setUserId(1);

        given(userRepository.existsById(anyInt()))
                .willReturn(true);
        given(answerRepository.existsById(anyInt()))
                .willReturn(true);

        AnswerRating answerRatingFromDb = new AnswerRating();
        answerRatingFromDb.setId(1);
        given(answerRatingRepository.findByAnswerIdAndUserId(anyInt(), anyInt()))
                .willReturn(Optional.of(answerRatingFromDb));

        // when
        underTest.rate(answerRating);

        // then
        verify(userRepository)
                .existsById(answerRating.getUserId());
        verify(answerRepository).existsById(answerRating.getAnswerId());
        verify(answerRatingRepository).findByAnswerIdAndUserId(answerRating.getAnswerId(), answerRating.getUserId());

        AnswerRating expected = new AnswerRating();
        expected.setId(1);
        expected.setUpvote(true);
        expected.setDownvote(false);
        expected.setAnswerId(1);
        expected.setUserId(1);
        verify(answerRatingRepository).save(expected);
    }

    @Test
    public void willThrowInRateWhenUserIdInvalid() {
        // given
        AnswerRating answerRating = new AnswerRating();
        answerRating.setAnswerId(1);
        answerRating.setUpvote(true);
        answerRating.setDownvote(false);
        answerRating.setUserId(1);

        // when
        // then
        assertThatThrownBy(() -> underTest.rate(answerRating))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid user id and answer id");
    }

    @Test
    public void willThrowInRateWhenAnswerIdInvalid() {
        // given
        AnswerRating answerRating = new AnswerRating();
        answerRating.setAnswerId(1);
        answerRating.setUpvote(true);
        answerRating.setDownvote(false);
        answerRating.setUserId(1);

        given(userRepository.existsById(anyInt()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.rate(answerRating))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid user id and answer id");
    }

    @Test
    public void willThrowInRateWhenUpvoteAndDownvote() {
        // given
        AnswerRating answerRating = new AnswerRating();
        answerRating.setAnswerId(1);
        answerRating.setUpvote(true);
        answerRating.setDownvote(true);
        answerRating.setUserId(1);

        given(userRepository.existsById(anyInt()))
                .willReturn(true);
        given(answerRepository.existsById(anyInt()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.rate(answerRating))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Upvote and downvote cannot be true");
    }
}