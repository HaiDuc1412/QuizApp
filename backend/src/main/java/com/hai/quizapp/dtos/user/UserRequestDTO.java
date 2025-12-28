package com.hai.quizapp.dtos.user;

import java.util.Set;

import com.hai.quizapp.validation.StrongPassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @StrongPassword(message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
        String password,
        @NotBlank(message = "Full name is required")
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName,
        Set<String> roles
        ) {

}
