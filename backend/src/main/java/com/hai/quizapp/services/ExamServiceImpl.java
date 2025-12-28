package com.hai.quizapp.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hai.quizapp.dtos.exam.AnswerSubmitDTO;
import com.hai.quizapp.dtos.exam.ExamResultDTO;
import com.hai.quizapp.dtos.exam.ExamSubmitRequestDTO;
import com.hai.quizapp.entities.Answer;
import com.hai.quizapp.entities.Question;
import com.hai.quizapp.entities.Quiz;
import com.hai.quizapp.entities.QuizSubmission;
import com.hai.quizapp.entities.User;
import com.hai.quizapp.enums.QuestionType;
import com.hai.quizapp.exceptions.ResourceNotFoundException;
import com.hai.quizapp.repositories.QuizRepository;
import com.hai.quizapp.repositories.QuizSubmissionRepository;
import com.hai.quizapp.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final QuizRepository quizRepository;
    private final UserRepository userRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;

    @Override
    @Transactional
    public ExamResultDTO submitExam(ExamSubmitRequestDTO requestDTO) {
        // Fetch quiz with questions and answers (solving N+1 problem)
        Quiz quiz = quizRepository.findByIdWithQuestionsAndAnswers(requestDTO.quizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + requestDTO.quizId()));

        // Fetch user
        User user = userRepository.findById(requestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + requestDTO.userId()));

        // Create a map of submitted answers
        Map<UUID, Set<UUID>> submittedAnswers = requestDTO.answers().stream()
                .collect(Collectors.toMap(
                        AnswerSubmitDTO::questionId,
                        AnswerSubmitDTO::selectedAnswerIds));

        // Calculate score
        int totalQuestions = quiz.getQuestions().size();
        int correctAnswers = 0;
        double totalScore = 0.0;

        for (Question question : quiz.getQuestions()) {
            Set<UUID> selectedAnswerIds = submittedAnswers.get(question.getId());

            if (selectedAnswerIds == null || selectedAnswerIds.isEmpty()) {
                continue; // No answer submitted for this question
            }

            // Get correct answer IDs
            Set<UUID> correctAnswerIds = question.getAnswers().stream()
                    .filter(Answer::isCorrect)
                    .map(Answer::getId)
                    .collect(Collectors.toSet());

            // Check if the answer is correct
            if (isAnswerCorrect(question, selectedAnswerIds, correctAnswerIds)) {
                correctAnswers++;
                totalScore += question.getScore();
            }
        }

        // Save submission
        QuizSubmission submission = QuizSubmission.builder()
                .user(user)
                .quiz(quiz)
                .score(totalScore)
                .submissionTime(LocalDateTime.now())
                .build();

        QuizSubmission savedSubmission = quizSubmissionRepository.save(submission);

        // Determine pass/fail (assuming 50% is passing)
        double maxScore = quiz.getQuestions().stream()
                .mapToInt(Question::getScore)
                .sum();
        boolean passed = totalScore >= (maxScore * 0.5);

        return new ExamResultDTO(
                savedSubmission.getId(),
                quiz.getId(),
                quiz.getTitle(),
                totalScore,
                totalQuestions,
                correctAnswers,
                passed,
                savedSubmission.getSubmissionTime());
    }

    private boolean isAnswerCorrect(Question question, Set<UUID> selectedAnswerIds, Set<UUID> correctAnswerIds) {
        if (question.getType() == QuestionType.SINGLE_CHOICE) {
            // For single choice, exactly one answer should be selected and it must be correct
            return selectedAnswerIds.size() == 1
                    && selectedAnswerIds.equals(correctAnswerIds);
        } else if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
            // For multiple choice, all selected answers must match exactly with correct answers
            return selectedAnswerIds.equals(correctAnswerIds);
        }
        return false;
    }
}
