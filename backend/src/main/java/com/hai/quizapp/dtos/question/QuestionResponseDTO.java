package com.hai.quizapp.dtos.question;

import java.util.List;
import java.util.UUID;

import com.hai.quizapp.dtos.answer.AnswerDTO;
import com.hai.quizapp.enums.QuestionType;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class QuestionResponseDTO {

    private UUID id;

    private String content;

    private QuestionType type;

    private Integer score;

    private List<AnswerDTO> answers;
}
