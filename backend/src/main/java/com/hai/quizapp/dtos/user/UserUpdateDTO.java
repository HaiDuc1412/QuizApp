package com.hai.quizapp.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
        @Email(message = "Invalid email format")
        String email,
        @Size(max = 100, message = "Full name must not exceed 100 characters")
        String fullName
        ) {

}
