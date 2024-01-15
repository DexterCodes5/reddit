package dev.dex.reddit.controller;

import dev.dex.reddit.models.requestmodels.AnswerRequest;
import dev.dex.reddit.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/answers")
@RequiredArgsConstructor
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping("")
    public ResponseEntity<?> postAnswer(@Valid @RequestBody AnswerRequest answerRequest) {
        answerService.save(answerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<?> getAnswers(@PathVariable int questionId) {
        return ResponseEntity.ok(answerService.findByQuestionId(questionId));
    }
}
