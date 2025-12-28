package com.hai.quizapp.dtos.answer;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AnswerDTO {

    private UUID id;

    @NotBlank(message = "Answer content must not be blank")
    private String content;

    @NotNull(message = "isCorrect must not be null")
    private Boolean isCorrect;
}
