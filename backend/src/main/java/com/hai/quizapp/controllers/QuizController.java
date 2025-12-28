package com.hai.quizapp.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hai.quizapp.dtos.ApiResponse;
import com.hai.quizapp.dtos.quiz.QuizRequestDTO;
import com.hai.quizapp.dtos.quiz.QuizResponseDTO;
import com.hai.quizapp.dtos.quiz.QuizSummaryDTO;
import com.hai.quizapp.services.QuizService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuizResponseDTO>> createQuiz(@Valid @RequestBody QuizRequestDTO requestDTO) {
        QuizResponseDTO quiz = quizService.createQuiz(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(quiz, "Quiz created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizResponseDTO>> getQuizById(@PathVariable UUID id) {
        QuizResponseDTO quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(ApiResponse.success(quiz, "Quiz retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<QuizSummaryDTO>>> getAllQuizzes(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        Page<QuizSummaryDTO> quizzes = quizService.getAllQuizzes(pageable);
        return ResponseEntity.ok(ApiResponse.success(quizzes, "Quizzes retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<QuizResponseDTO>> updateQuiz(
            @PathVariable UUID id,
            @Valid @RequestBody QuizRequestDTO requestDTO) {
        QuizResponseDTO quiz = quizService.updateQuiz(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(quiz, "Quiz updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuiz(@PathVariable UUID id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Quiz deleted successfully"));
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<QuizResponseDTO>> addQuestionsToQuiz(
            @PathVariable UUID id,
            @RequestBody List<UUID> questionIds) {
        QuizResponseDTO quiz = quizService.addQuestionsToQuiz(id, questionIds);
        return ResponseEntity.ok(ApiResponse.success(quiz, "Questions added to quiz successfully"));
    }

    @DeleteMapping("/{id}/questions")
    public ResponseEntity<ApiResponse<QuizResponseDTO>> removeQuestionsFromQuiz(
            @PathVariable UUID id,
            @RequestBody List<UUID> questionIds) {
        QuizResponseDTO quiz = quizService.removeQuestionsFromQuiz(id, questionIds);
        return ResponseEntity.ok(ApiResponse.success(quiz, "Questions removed from quiz successfully"));
    }
}
