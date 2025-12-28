package com.hai.quizapp.dtos.quiz;

import java.time.LocalDateTime;
import java.util.UUID;

public record QuizSummaryDTO(
        UUID id,
        String title,
        String description,
        Integer durationMinutes,
        Integer questionCount,
        Boolean isActive,
        LocalDateTime createdAt
        ) {

}
