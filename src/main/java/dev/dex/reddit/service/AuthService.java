package dev.dex.reddit.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dex.reddit.entity.user.Role;
import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.models.requestmodels.SignInRequest;
import dev.dex.reddit.models.requestmodels.SignUpRequest;
import dev.dex.reddit.models.responsemodel.AuthResponse;
import dev.dex.reddit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    public void signIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SignInRequest signInRequest = objectMapper.readValue(request.getInputStream(), SignInRequest.class);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signInRequest.getUsername(),
                signInRequest.getPassword()));
        User user = userRepository.findByUsername(signInRequest.getUsername())
                .orElseThrow();
        String accessToken = jwtService.generateToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        user.setRefreshToken(refreshToken);
        user = userRepository.save(user);
        AuthResponse authResponse = new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getImg(),
                accessToken);

        addJwtCookie(response, refreshToken);
        objectMapper.writeValue(response.getOutputStream(), authResponse);
    }

    public void signUp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        SignUpRequest signUpRequest = objectMapper.readValue(request.getInputStream(), SignUpRequest.class);

        String accessToken = jwtService.generateToken(signUpRequest.getUsername());
        String refreshToken = jwtService.generateRefreshToken(signUpRequest.getUsername());
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .active(true)
                .email(signUpRequest.getEmail())
                .role(Role.USER)
                .refreshToken(refreshToken)
                .build();
        user = userRepository.save(user);
        AuthResponse authResponse = new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getImg(),
                accessToken);

        addJwtCookie(response, refreshToken);
        objectMapper.writeValue(response.getOutputStream(), authResponse);
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals("jwt")) {
                    String refreshToken = cookie.getValue();
                    User user = userRepository.findByRefreshToken(refreshToken)
                            .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

                    // Validate JWT
                    if (jwtService.isTokenValid(refreshToken, user)) {
                        String accessToken = jwtService.generateToken(user.getUsername());
                        AuthResponse authResponse = new AuthResponse(user.getId(), user.getUsername(),
                                user.getEmail(), user.getImg(), accessToken);
                        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                    }
                }
            }
        }
    }

    private void addJwtCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("jwt", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) refreshTokenExpiration);
        cookie.setSecure(true);
        // This has to be set to work for all endpoints
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
