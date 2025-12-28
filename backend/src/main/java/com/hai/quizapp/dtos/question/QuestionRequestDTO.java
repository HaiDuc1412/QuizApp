package com.hai.quizapp.dtos.question;

import java.util.List;

import com.hai.quizapp.dtos.answer.AnswerDTO;
import com.hai.quizapp.enums.QuestionType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class QuestionRequestDTO {

    @NotBlank(message = "Content must not be blank")
    private String content;

    @NotNull(message = "Question type must not be null")
    private QuestionType type;

    @Min(value = 1, message = "Score must be at least 1")
    private Integer score;

    @NotEmpty(message = "Answers list must not be empty")
    private List<AnswerDTO> answers;
}
