package com.hai.quizapp.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.hai.quizapp.entities.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    // Prevent N+1 problem by eagerly loading answers
    @EntityGraph(attributePaths = {"answers"})
    @Query("SELECT q FROM Question q WHERE q.isActive = true")
    List<Question> findAllActive();

    @EntityGraph(attributePaths = {"answers"})
    @Query("SELECT q FROM Question q WHERE q.isActive = true")
    Page<Question> findAllActive(Pageable pageable);

    @EntityGraph(attributePaths = {"answers"})
    Optional<Question> findByIdAndIsActiveTrue(UUID id);
}
