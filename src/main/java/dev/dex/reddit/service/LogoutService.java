package dev.dex.reddit.service;

import dev.dex.reddit.repository.UserRepository;
import jakarta.servlet.http.Cookie;
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
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                if (cookie.getName().equals("jwt")) {
                    String refreshToken = cookie.getValue();
                    var user = userRepository.findByRefreshToken(refreshToken);
                    if (user.isPresent()) {
                        user.get().setRefreshToken(null);
                        userRepository.save(user.get());
                    }
                    break;
                }
            }
        }

        Cookie deleteCookie = new Cookie("jwt", null);
        deleteCookie.setMaxAge(0);
        deleteCookie.setPath("/");
        response.addCookie(deleteCookie);
    }
}
