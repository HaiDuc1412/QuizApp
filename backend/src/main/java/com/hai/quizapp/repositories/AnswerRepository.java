package com.hai.quizapp.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hai.quizapp.entities.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, UUID> {

    @EntityGraph(attributePaths = {"question"})
    @Query("SELECT a FROM Answer a WHERE a.id = :id")
    Optional<Answer> findByIdWithQuestion(@Param("id") UUID id);
}
