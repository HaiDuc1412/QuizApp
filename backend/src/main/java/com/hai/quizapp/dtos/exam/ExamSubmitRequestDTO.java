package com.hai.quizapp.dtos.exam;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ExamSubmitRequestDTO(
        @NotNull(message = "Quiz ID is required")
        UUID quizId,
        @NotNull(message = "User ID is required")
        UUID userId,
        @NotEmpty(message = "Answers are required")
        @Valid
        List<AnswerSubmitDTO> answers
        ) {

}
