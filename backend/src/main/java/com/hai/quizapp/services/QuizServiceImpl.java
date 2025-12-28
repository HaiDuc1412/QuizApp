package com.hai.quizapp.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hai.quizapp.dtos.answer.AnswerDTO;
import com.hai.quizapp.dtos.question.QuestionResponseDTO;
import com.hai.quizapp.dtos.quiz.QuizRequestDTO;
import com.hai.quizapp.dtos.quiz.QuizResponseDTO;
import com.hai.quizapp.dtos.quiz.QuizSummaryDTO;
import com.hai.quizapp.entities.Question;
import com.hai.quizapp.entities.Quiz;
import com.hai.quizapp.exceptions.ResourceNotFoundException;
import com.hai.quizapp.repositories.QuestionRepository;
import com.hai.quizapp.repositories.QuizRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public QuizResponseDTO createQuiz(QuizRequestDTO requestDTO) {
        Quiz quiz = Quiz.builder()
                .title(requestDTO.title())
                .description(requestDTO.description())
                .durationMinutes(requestDTO.durationMinutes())
                .questions(new HashSet<>())
                .build();

        // Add questions if provided
        if (requestDTO.questionIds() != null && !requestDTO.questionIds().isEmpty()) {
            Set<Question> questions = new HashSet<>(
                    questionRepository.findAllById(requestDTO.questionIds())
            );
            quiz.setQuestions(questions);
        }

        Quiz savedQuiz = quizRepository.save(quiz);
        return mapToResponseDTO(savedQuiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(UUID id) {
        Quiz quiz = quizRepository.findByIdWithQuestionsAndAnswers(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        return mapToResponseDTO(quiz);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizSummaryDTO> getAllQuizzes(Pageable pageable) {
        return quizRepository.findAll(pageable)
                .map(this::mapToSummaryDTO);
    }

    @Override
    @Transactional
    public QuizResponseDTO updateQuiz(UUID id, QuizRequestDTO requestDTO) {
        Quiz quiz = quizRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));

        quiz.setTitle(requestDTO.title());
        quiz.setDescription(requestDTO.description());
        quiz.setDurationMinutes(requestDTO.durationMinutes());

        // Update questions if provided
        if (requestDTO.questionIds() != null) {
            Set<Question> questions = new HashSet<>(
                    questionRepository.findAllById(requestDTO.questionIds())
            );
            quiz.setQuestions(questions);
        }

        Quiz updatedQuiz = quizRepository.save(quiz);
        return mapToResponseDTO(updatedQuiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(UUID id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));

        // Soft delete
        quiz.setIsActive(false);
        quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public QuizResponseDTO addQuestionsToQuiz(UUID quizId, List<UUID> questionIds) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        List<Question> questionsToAdd = questionRepository.findAllById(questionIds);
        quiz.getQuestions().addAll(questionsToAdd);

        Quiz updatedQuiz = quizRepository.save(quiz);
        return mapToResponseDTO(updatedQuiz);
    }

    @Override
    @Transactional
    public QuizResponseDTO removeQuestionsFromQuiz(UUID quizId, List<UUID> questionIds) {
        Quiz quiz = quizRepository.findByIdWithQuestions(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        quiz.getQuestions().removeIf(question -> questionIds.contains(question.getId()));

        Quiz updatedQuiz = quizRepository.save(quiz);
        return mapToResponseDTO(updatedQuiz);
    }

    private QuizResponseDTO mapToResponseDTO(Quiz quiz) {
        Set<QuestionResponseDTO> questionDTOs = quiz.getQuestions().stream()
                .map(question -> QuestionResponseDTO.builder()
                .id(question.getId())
                .content(question.getContent())
                .type(question.getType())
                .score(question.getScore())
                .answers(question.getAnswers().stream()
                        .map(answer -> new AnswerDTO(
                        answer.getId(),
                        answer.getContent(),
                        answer.isCorrect()))
                        .collect(Collectors.toList()))
                .build())
                .collect(Collectors.toSet());

        return new QuizResponseDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getDurationMinutes(),
                questionDTOs,
                quiz.getIsActive(),
                quiz.getCreatedAt(),
                quiz.getUpdatedAt());
    }

    private QuizSummaryDTO mapToSummaryDTO(Quiz quiz) {
        return new QuizSummaryDTO(
                quiz.getId(),
                quiz.getTitle(),
                quiz.getDescription(),
                quiz.getDurationMinutes(),
                quiz.getQuestions() != null ? quiz.getQuestions().size() : 0,
                quiz.getIsActive(),
                quiz.getCreatedAt());
    }
}
