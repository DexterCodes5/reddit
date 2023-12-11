package dev.dex.reddit.controller;

import dev.dex.reddit.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public void signIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.signIn(request, response);
    }

    @PostMapping("/sign-up")
    public void signUp(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.signUp(request, response);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }
}
