package com.spboot.auth.controller;

import com.spboot.auth.dto.UpdateUserRequestDto;
import com.spboot.auth.dto.UserRequestDto;
import com.spboot.auth.dto.UserResponseDto;
import com.spboot.auth.service.UserService;
import com.spboot.auth.service.UserService2;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController2 {
    private final UserService2 userService;

    // ✅ Create User
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(
            @Valid @RequestBody UserRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(dto));
    }

    // ✅ Get All Users
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ✅ Get User by Email
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(
            @PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    // ✅ Get User by ID
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(
            @PathVariable String userId) {

        return ResponseEntity.ok(userService.getUserById(userId));
    }

    // ✅ Update User
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(
            @Valid @RequestBody UpdateUserRequestDto dto,
            @PathVariable String userId) {

        return ResponseEntity.ok(userService.updateUser(dto, userId));
    }

    // ✅ Delete User
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {

        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}