package dev.dex.reddit.controller;

import dev.dex.reddit.entity.QuestionRating;
import dev.dex.reddit.service.QuestionRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/question-ratings")
@RequiredArgsConstructor
public class QuestionRatingController {
    private final QuestionRatingService questionRatingService;

    @PostMapping("")
    public ResponseEntity<?> rateQuestion(@Valid @RequestBody QuestionRating questionRating) {
        questionRatingService.rate(questionRating);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<?> getUserQuestionRating(@PathVariable int questionId, Principal principal) {
        return ResponseEntity.ok(questionRatingService.getUserQuestionRating(questionId, principal));
    }
}
