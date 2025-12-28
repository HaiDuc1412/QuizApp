package com.hai.quizapp.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hai.quizapp.dtos.question.QuestionRequestDTO;
import com.hai.quizapp.dtos.question.QuestionResponseDTO;

public interface QuestionService {

    QuestionResponseDTO create(QuestionRequestDTO questionRequestDTO);

    List<QuestionResponseDTO> getAll();

    Page<QuestionResponseDTO> getAllPaginated(Pageable pageable);

    QuestionResponseDTO getById(UUID id);

    QuestionResponseDTO update(UUID id, QuestionRequestDTO questionRequestDTO);

    void delete(UUID id);
}
