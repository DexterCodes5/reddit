package dev.dex.reddit.controller;

import dev.dex.reddit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @PatchMapping("")
    public ResponseEntity<?> updateUser(@RequestParam MultipartFile image, @RequestParam String username, Principal principal) {
        return ResponseEntity.ok(userService.updateUser(image, username, principal));
    }
}
