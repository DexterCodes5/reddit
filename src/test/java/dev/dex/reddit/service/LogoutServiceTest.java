package dev.dex.reddit.service;

import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {
    @Mock
    private UserRepository userRepository;
    private LogoutService underTest;

    @BeforeEach
    void setUp() {
        underTest = new LogoutService(userRepository);
    }

    @Test
    void canLogout() {
        // given
        String accessToken = "accessToken";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken);

        MockHttpServletResponse response = new MockHttpServletResponse();

        User user = new User(1, "dexter", "test123", true,
                null, "dexter@mail.com", Role.USER, null, accessToken, "refreshToken",
                null);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        given(userRepository.findByAccessToken(anyString()))
                .willReturn(Optional.of(user));

        // when
        underTest.logout(request, response, authentication);

        // then
        verify(userRepository).findByAccessToken(accessToken);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getAccessToken()).isNull();
        assertThat(capturedUser.getRefreshToken()).isNull();
    }
}