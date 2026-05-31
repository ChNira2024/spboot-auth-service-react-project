package com.spboot.auth.service.impl;

import com.spboot.auth.config.AppConstants;
import com.spboot.auth.dto.UpdateUserRequestDto;
import com.spboot.auth.dto.UserRequestDto;
import com.spboot.auth.dto.UserResponseDto;
import com.spboot.auth.entity.Role;
import com.spboot.auth.entity.User;
import com.spboot.auth.exception.ResourceNotFoundException;
import com.spboot.auth.exception.UserAlreadyExistsException;
import com.spboot.auth.helper.UserHelper;
import com.spboot.auth.repository.UserRepository;
import com.spboot.auth.security.repository.RoleRepository;
import com.spboot.auth.service.UserService2;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl2 implements UserService2 {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl2.class);

    @Override
    public UserResponseDto createUser(UserRequestDto dto) {
        log.info("Creating user with email: {}", dto.getEmail());

        // check existing user
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserAlreadyExistsException(
                    "User already exists with email: " + dto.getEmail()
            );
        }

        // map DTO → Entity
        User user = modelMapper.map(dto, User.class);

        // IMPORTANT: assign default ROLE_USER BEFORE saving
        Role roleUser = roleRepository.findByName("ROLE_" + AppConstants.ADMIN_ROLE)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        user.setRoles(Set.of(roleUser));
        // save user (with roles)
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getEmail());

        // convert to response
        return mapToResponse(savedUser);
    }

    @Override
    public UserResponseDto getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }

    @Override
    public UserResponseDto updateUser(UpdateUserRequestDto dto, String userId) {
        UUID id;
        try {
            id = UUID.fromString(userId);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid userId format");
        }

        log.info("Updating user with id: {}", userId);
        User existingUser = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        boolean isUpdated = false;

        // Update name
        if (dto.getName() != null && !dto.getName().isBlank()) {
            existingUser.setName(dto.getName());
            isUpdated = true;
        }

        // Update password (encoded)
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(dto.getPassword()));
            isUpdated = true;
        }

        // Update image
        if (dto.getImage() != null && !dto.getImage().isBlank()) {
            existingUser.setImage(dto.getImage());
            isUpdated = true;
        }

        // Update enabled flag
        if (dto.getEnabled() != null) {
            existingUser.setEnable(dto.getEnabled());
            isUpdated = true;
        }

        // Email should NEVER be updated

        if (!isUpdated) {
            log.warn("No valid fields provided for update for user id: {}", userId);
            throw new IllegalArgumentException("No valid fields provided for update");
        }
        existingUser.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully: {}", updatedUser.getEmail());
        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        UUID id = UUID.fromString(userId);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
        log.info("User deleted: {}", user.getEmail());
    }

    @Override
    public UserResponseDto getUserById(String userId) {
        User user = userRepository.findById(UserHelper.parseUUID(userId)).orElseThrow(() -> new ResourceNotFoundException("User not found with given id"));
        return mapToResponse(user);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(user -> UserResponseDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .image(user.getImage())
                        .enabled(user.isEnabled())
                        .createdAt(user.getCreatedAt())
                        .roles(user.getRoles())
                        .build()
                )
                .toList();
    }

    private UserResponseDto mapToResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .image(user.getImage())
                .email(user.getEmail())
                .name(user.getName())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles())
                .build();
    }
}
