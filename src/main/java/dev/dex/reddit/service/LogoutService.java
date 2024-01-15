package dev.dex.reddit.service;

import dev.dex.reddit.entity.user.User;
import dev.dex.reddit.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        jwt = authHeader.substring(7);
        User user = userRepository.findByAccessToken(jwt)
                .orElseThrow(() -> new RuntimeException("Invalid access token"));
        user.setAccessToken(null);
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}
