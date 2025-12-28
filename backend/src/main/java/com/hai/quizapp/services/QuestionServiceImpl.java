package com.hai.quizapp.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hai.quizapp.dtos.answer.AnswerDTO;
import com.hai.quizapp.dtos.question.QuestionRequestDTO;
import com.hai.quizapp.dtos.question.QuestionResponseDTO;
import com.hai.quizapp.entities.Answer;
import com.hai.quizapp.entities.Question;
import com.hai.quizapp.exceptions.ResourceNotFoundException;
import com.hai.quizapp.repositories.QuestionRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public QuestionResponseDTO create(QuestionRequestDTO request) {
        Question question = Question.builder()
                .content(request.getContent())
                .type(request.getType())
                .score(request.getScore())
                .build();

        List<Answer> answers = request.getAnswers().stream()
                .map(a -> Answer.builder()
                .content(a.content())
                .isCorrect(a.isCorrect())
                .question(question)
                .build())
                .collect(Collectors.toList());

        question.setAnswers(answers);
        question.setIsActive(true);

        Question saved = questionRepository.save(question);

        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getAll() {
        return questionRepository.findAllActive().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuestionResponseDTO> getAllPaginated(Pageable pageable) {
        return questionRepository.findAllActive(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDTO getById(UUID id) {
        Question question = questionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        return mapToResponseDTO(question);
    }

    @Override
    public QuestionResponseDTO update(UUID id, QuestionRequestDTO request) {
        Question question = questionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        // Update question fields
        question.setContent(request.getContent());
        question.setType(request.getType());
        question.setScore(request.getScore());

        // Sync answers - remove old ones and add new ones
        question.getAnswers().clear();

        List<Answer> newAnswers = request.getAnswers().stream()
                .map(a -> Answer.builder()
                .content(a.content())
                .isCorrect(a.isCorrect())
                .question(question)
                .build())
                .collect(Collectors.toList());

        question.getAnswers().addAll(newAnswers);

        Question updated = questionRepository.save(question);

        return mapToResponseDTO(updated);
    }

    @Override
    public void delete(UUID id) {
        Question question = questionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + id));

        // Soft delete
        question.setIsActive(false);
        questionRepository.save(question);
    }

    private QuestionResponseDTO mapToResponseDTO(Question question) {
        List<AnswerDTO> answerDTOs = question.getAnswers().stream()
                .map(a -> new AnswerDTO(
                a.getId(),
                a.getContent(),
                a.isCorrect()))
                .collect(Collectors.toList());

        return QuestionResponseDTO.builder()
                .id(question.getId())
                .content(question.getContent())
                .type(question.getType())
                .score(question.getScore())
                .answers(answerDTOs)
                .build();
    }
}
