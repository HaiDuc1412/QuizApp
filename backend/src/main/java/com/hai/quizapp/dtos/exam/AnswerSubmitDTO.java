package com.hai.quizapp.dtos.exam;

import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AnswerSubmitDTO(
        @NotNull(message = "Question ID is required")
        UUID questionId,
        @NotNull(message = "Selected answer IDs are required")
        Set<UUID> selectedAnswerIds
        ) {

}
