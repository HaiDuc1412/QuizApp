package com.hai.quizapp.dtos.user;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email,
        String fullName,
        Set<String> roles,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}
