package com.hai.quizapp.dtos.quiz;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.hai.quizapp.dtos.question.QuestionResponseDTO;

public record QuizResponseDTO(
        UUID id,
        String title,
        String description,
        Integer durationMinutes,
        Set<QuestionResponseDTO> questions,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}
