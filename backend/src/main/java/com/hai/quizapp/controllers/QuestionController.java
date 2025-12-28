package com.hai.quizapp.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hai.quizapp.dtos.ApiResponse;
import com.hai.quizapp.dtos.question.QuestionRequestDTO;
import com.hai.quizapp.dtos.question.QuestionResponseDTO;
import com.hai.quizapp.services.QuestionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Validated
@Tag(name = "Question Management", description = "APIs for managing questions and answers")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @Operation(summary = "Create new question", description = "Create a new question with answers")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> createQuestion(
            @Valid @RequestBody QuestionRequestDTO request) {

        QuestionResponseDTO created = questionService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.<QuestionResponseDTO>builder()
                        .status(HttpStatus.CREATED.value())
                        .message("Question created successfully")
                        .data(created)
                        .build());
    }

    @GetMapping
    @Operation(summary = "Get all questions", description = "Get all active questions with optional pagination")
    public ResponseEntity<ApiResponse<?>> getAllQuestions(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String sortDir,
            @Parameter(description = "Enable pagination")
            @RequestParam(defaultValue = "true") boolean paginate) {

        if (paginate) {
            Sort sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<QuestionResponseDTO> questions = questionService.getAllPaginated(pageable);

            return ResponseEntity.ok(ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Questions retrieved successfully")
                    .data(questions)
                    .build());
        } else {
            List<QuestionResponseDTO> questions = questionService.getAll();

            return ResponseEntity.ok(ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("Questions retrieved successfully")
                    .data(questions)
                    .build());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID", description = "Get question details with all answers")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> getQuestionById(
            @PathVariable UUID id) {

        QuestionResponseDTO question = questionService.getById(id);

        return ResponseEntity.ok(ApiResponse.<QuestionResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Question retrieved successfully")
                .data(question)
                .build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update question", description = "Update question content and sync answers")
    public ResponseEntity<ApiResponse<QuestionResponseDTO>> updateQuestion(
            @PathVariable UUID id,
            @Valid @RequestBody QuestionRequestDTO request) {

        QuestionResponseDTO updated = questionService.update(id, request);

        return ResponseEntity.ok(ApiResponse.<QuestionResponseDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Question updated successfully")
                .data(updated)
                .build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete question", description = "Soft delete question (set isActive = false)")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable UUID id) {

        questionService.delete(id);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.<Void>builder()
                        .status(HttpStatus.NO_CONTENT.value())
                        .message("Question deleted successfully")
                        .build());
    }
}
