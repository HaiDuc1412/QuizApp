package com.hai.quizapp.services;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hai.quizapp.dtos.user.UserRequestDTO;
import com.hai.quizapp.dtos.user.UserResponseDTO;
import com.hai.quizapp.dtos.user.UserUpdateDTO;
import com.hai.quizapp.entities.Role;
import com.hai.quizapp.entities.User;
import com.hai.quizapp.exceptions.ResourceNotFoundException;
import com.hai.quizapp.repositories.RoleRepository;
import com.hai.quizapp.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO requestDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(requestDTO.email())) {
            throw new IllegalArgumentException("Email already exists: " + requestDTO.email());
        }

        // Get roles
        Set<Role> roles = new HashSet<>();
        if (requestDTO.roles() != null && !requestDTO.roles().isEmpty()) {
            roles = requestDTO.roles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
                    .collect(Collectors.toSet());
        } else {
            // Default role
            Role defaultRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role newRole = Role.builder().name("ROLE_USER").build();
                        return roleRepository.save(newRole);
                    });
            roles.add(defaultRole);
        }

        User user = User.builder()
                .email(requestDTO.email())
                .password(passwordEncoder.encode(requestDTO.password()))
                .fullName(requestDTO.fullName())
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        return mapToResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToResponseDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::mapToResponseDTO);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(UUID id, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (updateDTO.email() != null && !updateDTO.email().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.email())) {
                throw new IllegalArgumentException("Email already exists: " + updateDTO.email());
            }
            user.setEmail(updateDTO.email());
        }

        if (updateDTO.fullName() != null) {
            user.setFullName(updateDTO.fullName());
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Soft delete
        user.setIsActive(false);
        userRepository.save(user);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                roleNames,
                user.getIsActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
