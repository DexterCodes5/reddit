package dev.dex.reddit.controller;

import dev.dex.reddit.models.requestmodels.QuestionRequest;
import dev.dex.reddit.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {
    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("")
    public ResponseEntity<?> postQuestion(@RequestBody QuestionRequest questionRequest) {
        questionService.save(questionRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/get-questions")
    public ResponseEntity<?> getQuestions(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(questionService.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable int id) {
        return ResponseEntity.ok(questionService.findById(id));
    }

    @GetMapping("/get-questions-by-user/{username}")
    public ResponseEntity<?> getQuestionsByUser(@PathVariable String username) {
        return ResponseEntity.ok(questionService.findByUser(username));
    }
}
