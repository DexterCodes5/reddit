package dev.dex.reddit.controller;

import dev.dex.reddit.models.Image;
import dev.dex.reddit.service.StorageService;
import dev.dex.reddit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/image-data")
@RequiredArgsConstructor
public class ImageDataController {
    private final StorageService storageService;
    private final UserService userService;

    @GetMapping("/user/{image}")
    public ResponseEntity<?> getUserImage(@PathVariable String image) throws IOException {
        Image imageFile = storageService.downloadUserImage(image);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(imageFile.getType()))
                .body(imageFile.getImg());
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<?> uploadUserImage(@PathVariable int userId, @RequestParam MultipartFile image) throws IOException {
        userService.uploadUserImage(userId, image);
        return ResponseEntity.ok("Image uploaded successfully");
    }
}
