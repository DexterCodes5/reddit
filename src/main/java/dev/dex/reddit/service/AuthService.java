package dev.dex.reddit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.exc.DuplicateKeyException;
import dev.dex.reddit.models.requestmodels.ChangePasswordRequest;
import dev.dex.reddit.models.requestmodels.ForgotPasswordRequest;
import dev.dex.reddit.models.requestmodels.SignInRequest;
import dev.dex.reddit.models.requestmodels.SignUpRequest;
import dev.dex.reddit.models.responsemodels.AuthResponse;
import dev.dex.reddit.repository.UserRepository;
import dev.dex.reddit.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final Logger LOG = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MailService mailService;
    @Value("${frontend-url}")
    private String frontendUrl;
    @Value("${application.base-url}")
    private String baseUrl;

    public AuthResponse signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(),
                signInRequest.getPassword()));
        User user = userRepository.findByUsername(signInRequest.getUsername())
                .orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        String accessToken = jwtService.generateToken(claims, user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);
        user = userRepository.save(user);
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getImg(),
                accessToken, refreshToken);
    }

    public void signUp(SignUpRequest signUpRequest) throws IOException {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new DuplicateKeyException("Username is already taken.");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new DuplicateKeyException("Email is already taken.");
        }

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .active(false)
                .verificationCode(StringUtils.generateRandomString(64))
                .email(signUpRequest.getEmail())
                .role(Role.USER)
                .img(baseUrl + "/api/v1/image-data/user/user.jpg")
                .build();
        user = userRepository.save(user);
        try {
            mailService.sendVerificationEmail(user);
        } catch (MessagingException ex) {
            LOG.error("SendVerificationEmail failed");
        }
    }

    public void verify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String verificationCode = request.getParameter("code");

        User user = userRepository.findByVerificationCode(verificationCode)
                .orElseThrow(() -> new RuntimeException("Invalid Verification Code"));
        user.setVerificationCode(null);
        user.setActive(true);
        userRepository.save(user);

        response.sendRedirect(frontendUrl + "?verified=true");
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        Optional<User> user = userRepository.findByRefreshToken(jwt);
        if (user.isPresent() && jwtService.isTokenValid(jwt, user.get())) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", user.get().getEmail());
            String accessToken = jwtService.generateToken(claims, user.get().getUsername());
            user.get().setAccessToken(accessToken);
            User savedUser = userRepository.save(user.get());
            AuthResponse authResponse = new AuthResponse(savedUser.getId(), savedUser.getUsername(),
                    savedUser.getEmail(), savedUser.getImg(), savedUser.getAccessToken(), savedUser.getRefreshToken());
            new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
        }
    }

    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email"));
        user.setForgotPasswordCode(StringUtils.generateRandomString(64));
        userRepository.save(user);

        try {
            mailService.sendForgotPasswordEmail(user);
        } catch (MessagingException ex) {
            LOG.error("Send Forgot Password Email failed: " + ex);
        }
    }

    public void forgotPasswordRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String forgotPasswordCode = request.getParameter("code");

        userRepository.findByForgotPasswordCode(forgotPasswordCode)
                .orElseThrow(() -> new RuntimeException("Invalid forgot password code."));

        response.sendRedirect(frontendUrl + "/change-password?code=" + forgotPasswordCode);
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findByForgotPasswordCode(changePasswordRequest.forgotPasswordCode())
                .orElseThrow(() -> new RuntimeException("Invalid forgot password code"));
        user.setForgotPasswordCode(null);
        user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
        userRepository.save(user);
    }
}
