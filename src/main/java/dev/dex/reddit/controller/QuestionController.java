package dev.dex.reddit.controller;

import dev.dex.reddit.entity.Question;
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
    public ResponseEntity<?> postQuestion(@RequestBody Question question) {
        questionService.save(question);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("")
    public ResponseEntity<?> getQuestions() {
        return ResponseEntity.status(HttpStatus.OK).body(questionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable int id) {
        return ResponseEntity.ok(questionService.findById(id));
    }
}
