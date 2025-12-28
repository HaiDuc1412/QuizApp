package com.hai.quizapp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hai.quizapp.dtos.ApiResponse;
import com.hai.quizapp.dtos.exam.ExamResultDTO;
import com.hai.quizapp.dtos.exam.ExamSubmitRequestDTO;
import com.hai.quizapp.services.ExamService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/exam")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ExamResultDTO>> submitExam(
            @Valid @RequestBody ExamSubmitRequestDTO requestDTO) {
        ExamResultDTO result = examService.submitExam(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(result, "Exam submitted successfully"));
    }
}
