package com.hai.quizapp.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hai.quizapp.entities.QuizSubmission;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, UUID> {

    List<QuizSubmission> findByUserId(UUID userId);

    List<QuizSubmission> findByQuizId(UUID quizId);
}
