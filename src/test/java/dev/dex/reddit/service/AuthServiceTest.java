package dev.dex.reddit.service;

import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.requestmodels.ChangePasswordRequest;
import dev.dex.reddit.models.requestmodels.ForgotPasswordRequest;
import dev.dex.reddit.models.requestmodels.SignInRequest;
import dev.dex.reddit.models.requestmodels.SignUpRequest;
import dev.dex.reddit.models.responsemodels.AuthResponse;
import dev.dex.reddit.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private MailService mailService;
    private AuthService underTest;

    private String frontendUrl = "http://localhost:3000";
    private String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        underTest = new AuthService(userRepository, passwordEncoder, jwtService, authenticationManager, mailService);
    }

    @Test
    void canSignIn() {
        // given
        String username = "dexter";
        String password = "test123";
        SignInRequest signInRequest = new SignInRequest(username, password);

        User user = new User(1, username, password, true, null, "dexter@mail.com", Role.USER,
                "img", null, null, null);
        given(userRepository.findByUsername(anyString()))
                .willReturn(Optional.of(user));

        String accessToken = "accessToken";
        given(jwtService.generateToken(any(), anyString()))
                .willReturn(accessToken);

        String refreshToken = "refreshToken";
        given(jwtService.generateRefreshToken(anyString()))
                .willReturn(refreshToken);

        User userWithTokens = new User(1, username, password, true, null, "dexter@mail.com", Role.USER,
                "img", accessToken, refreshToken, null);
        given(userRepository.save(any(User.class)))
                .willReturn(userWithTokens);

        // when
        AuthResponse res = underTest.signIn(signInRequest);

        // then
        verify(authenticationManager).authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(),
                signInRequest.getPassword()));
        verify(userRepository).findByUsername(username);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        verify(jwtService).generateToken(claims, username);
        verify(jwtService).generateRefreshToken(username);

        verify(userRepository).save(userWithTokens);

        assertThat(res).isEqualTo(new AuthResponse(userWithTokens.getId(), userWithTokens.getUsername(), userWithTokens.getEmail(),
                userWithTokens.getImg(), accessToken, refreshToken));
    }

    @Test
    void willThrowInSignInWhenInvalidUsername() {
        // given
        String username = "dexter";
        String password = "test123";
        SignInRequest signInRequest = new SignInRequest(username, password);

        // when
        // then
        assertThatThrownBy(() -> underTest.signIn(signInRequest));
    }

    @Test
    void canSignUp() throws IOException, MessagingException {
        // given
        String username = "dexter";
        String password = "test123";
        String email = "dexter@mail.com";
        SignUpRequest signUpRequest = new SignUpRequest(username, password, email);

        String encodedPassword = "encoded password";
        given(passwordEncoder.encode(anyString()))
                .willReturn(encodedPassword);

        String img = baseUrl + "/api/v1/image-data/user/user.jpg";
        User user = new User(1, username, encodedPassword, false, null, email, Role.USER,
                img, null, null, null);
        given(userRepository.save(any(User.class)))
                .willReturn(user);

        // when
        underTest.signUp(signUpRequest);

        // then
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);

        verify(passwordEncoder).encode(password);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(capturedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(capturedUser.getActive()).isEqualTo(user.getActive());
        assertThat(capturedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(capturedUser.getRole()).isEqualTo(user.getRole());

        verify(mailService).sendVerificationEmail(user);
    }

    @Test
    void willThrowInSignUpWhenUsernameTaken() {
        // given
        String username = "dexter";
        String password = "test123";
        String email = "dexter@mail.com";
        SignUpRequest signUpRequest = new SignUpRequest(username, password, email);

        given(userRepository.existsByUsername(anyString()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.signUp(signUpRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username is already taken.");
    }

    @Test
    void willThrowInSignUpWhenEmailTaken() {
        // given
        String username = "dexter";
        String password = "test123";
        String email = "dexter@mail.com";
        SignUpRequest signUpRequest = new SignUpRequest(username, password, email);

        given(userRepository.existsByEmail(anyString()))
                .willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.signUp(signUpRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email is already taken.");
    }

    @Test
    void canVerify() throws IOException {
        // given
        String verificationCode = "aaaaaa";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("code", verificationCode);

        MockHttpServletResponse response = new MockHttpServletResponse();

        User user = new User(1, "dexter", "test123", false, verificationCode, "dexter@mail.com",
                Role.USER, "img", null, null, null);
        given(userRepository.findByVerificationCode(anyString()))
                .willReturn(Optional.of(user));

        // when
        underTest.verify(request, response);

        // then
        verify(userRepository).findByVerificationCode(verificationCode);

        User expectedUser = new User(1, "dexter", "test123", true, null, "dexter@mail.com",
                Role.USER, "img", null, null, null);
        verify(userRepository).save(expectedUser);
    }

    @Test
    void refreshToken() throws IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String refreshToken = "refreshToken";
        request.addHeader("Authorization", "Bearer " + refreshToken);

        MockHttpServletResponse response = new MockHttpServletResponse();

        User user = new User(1, "dexter", "test123", true, null, "dexter@mail.com",
                Role.USER, "img", null, refreshToken, null);
        given(userRepository.findByRefreshToken(anyString()))
                .willReturn(Optional.of(user));

        given(jwtService.isTokenValid(anyString(), any(UserDetails.class)))
                .willReturn(true);

        String accessToken = "accessToken";
        given(jwtService.generateToken(anyMap(), anyString()))
                .willReturn(accessToken);

        given(userRepository.save(any(User.class)))
                .willReturn(user);

        // when
        underTest.refreshToken(request, response);

        // then
        verify(userRepository).findByRefreshToken(refreshToken);

        verify(jwtService).isTokenValid(refreshToken, user);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        verify(jwtService).generateToken(claims, user.getUsername());

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getAccessToken()).isEqualTo(accessToken);
    }

    @Test
    void canForgotPassword() throws MessagingException {
        // given
        String email = "dexter@mail.com";
        ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail(email);

        User user = new User(1, "dexter", "test123", true, null, "dexter@mail.com",
                Role.USER, "img", null, null, null);
        given(userRepository.findByEmail(anyString()))
                .willReturn(Optional.of(user));

        // when
        underTest.forgotPassword(forgotPasswordRequest);

        // then
        verify(userRepository).save(any(User.class));

        verify(mailService).sendForgotPasswordEmail(any(User.class));
    }

    @Test
    void forgotPasswordRedirect() throws IOException {
        // given
        String forgotPasswordCode = "aaaa";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("code", forgotPasswordCode);

        MockHttpServletResponse response = new MockHttpServletResponse();

        User user = new User(1, "dexter", "test123", true, null, "dexter@mail.com",
                Role.USER, "img", null, null, null);
        given(userRepository.findByForgotPasswordCode(anyString()))
                .willReturn(Optional.of(user));

        // when
        underTest.forgotPasswordRedirect(request, response);
    }

    @Test
    void changePassword() {
        // given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("aaaaaa", "test123");

        User user = new User(1, "dexter", "test123", true, null, "dexter@mail.com",
                Role.USER, "img", null, null, null);
        given(userRepository.findByForgotPasswordCode(anyString()))
                .willReturn(Optional.of(user));

        String encodedPassword = "encoded password";
        given(passwordEncoder.encode(anyString()))
                .willReturn(encodedPassword);

        // when
        underTest.changePassword(changePasswordRequest);

        // then
        verify(userRepository).findByForgotPasswordCode(changePasswordRequest.forgotPasswordCode());
        verify(passwordEncoder).encode(changePasswordRequest.newPassword());

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getForgotPasswordCode()).isNull();
        assertThat(capturedUser.getPassword()).isEqualTo(encodedPassword);
    }
}