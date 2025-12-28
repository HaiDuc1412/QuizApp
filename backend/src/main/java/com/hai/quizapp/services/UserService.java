package com.hai.quizapp.services;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hai.quizapp.dtos.user.UserRequestDTO;
import com.hai.quizapp.dtos.user.UserResponseDTO;
import com.hai.quizapp.dtos.user.UserUpdateDTO;

public interface UserService {

    UserResponseDTO createUser(UserRequestDTO requestDTO);

    UserResponseDTO getUserById(UUID id);

    Page<UserResponseDTO> getAllUsers(Pageable pageable);

    UserResponseDTO updateUser(UUID id, UserUpdateDTO updateDTO);

    void deleteUser(UUID id);
}
