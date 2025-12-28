package com.hai.quizapp.dtos.answer;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AnswerDTO(
        UUID id,
        @NotBlank(message = "Answer content must not be blank")
        String content,
        @NotNull(message = "isCorrect must not be null")
        Boolean isCorrect
        ) {

}
