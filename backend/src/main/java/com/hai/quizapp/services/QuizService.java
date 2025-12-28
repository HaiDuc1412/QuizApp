package com.hai.quizapp.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hai.quizapp.dtos.quiz.QuizRequestDTO;
import com.hai.quizapp.dtos.quiz.QuizResponseDTO;
import com.hai.quizapp.dtos.quiz.QuizSummaryDTO;

public interface QuizService {

    QuizResponseDTO createQuiz(QuizRequestDTO requestDTO);

    QuizResponseDTO getQuizById(UUID id);

    Page<QuizSummaryDTO> getAllQuizzes(Pageable pageable);

    QuizResponseDTO updateQuiz(UUID id, QuizRequestDTO requestDTO);

    void deleteQuiz(UUID id);

    QuizResponseDTO addQuestionsToQuiz(UUID quizId, List<UUID> questionIds);

    QuizResponseDTO removeQuestionsFromQuiz(UUID quizId, List<UUID> questionIds);
}
