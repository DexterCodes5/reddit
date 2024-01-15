package dev.dex.reddit.controller;

import dev.dex.reddit.entity.AnswerRating;
import dev.dex.reddit.service.AnswerRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/answer-ratings")
@RequiredArgsConstructor
public class AnswerRatingController {
    private final AnswerRatingService answerRatingService;

    @GetMapping("/{answerId}")
    public ResponseEntity<?> getAnswerRating(@PathVariable int answerId, Principal principal) {
        return ResponseEntity.ok(answerRatingService.getUserAnswerRating(answerId, principal));
    }

    @PostMapping("")
    public ResponseEntity<?> postAnswerRating(@Valid @RequestBody AnswerRating answerRating) {
        answerRatingService.rate(answerRating);
        return ResponseEntity.ok().build();
    }
}
