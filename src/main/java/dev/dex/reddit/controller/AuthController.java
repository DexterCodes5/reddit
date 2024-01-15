package dev.dex.reddit.controller;

import dev.dex.reddit.models.requestmodels.ChangePasswordRequest;
import dev.dex.reddit.models.requestmodels.ForgotPasswordRequest;
import dev.dex.reddit.models.requestmodels.SignInRequest;
import dev.dex.reddit.models.requestmodels.SignUpRequest;
import dev.dex.reddit.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest signInRequest) {
        return ResponseEntity.ok(authService.signIn(signInRequest));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) throws IOException {
        authService.signUp(signUpRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify")
    public void verify(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.verify(request, response);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.refreshToken(request, response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authService.forgotPassword(forgotPasswordRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/forgot-password-redirect")
    public void forgotPasswordRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authService.forgotPasswordRedirect(request, response);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest);
        return ResponseEntity.ok().build();
    }
}
