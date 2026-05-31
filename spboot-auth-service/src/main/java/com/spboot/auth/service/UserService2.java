package com.spboot.auth.service;

import com.spboot.auth.dto.UpdateUserRequestDto;
import com.spboot.auth.dto.UserDto;
import com.spboot.auth.dto.UserRequestDto;
import com.spboot.auth.dto.UserResponseDto;

import java.util.List;


public interface UserService2 {

    //create user
    UserResponseDto createUser(UserRequestDto userDto);

    //get user by email
    UserResponseDto getUserByEmail(String email);

    //update user
    UserResponseDto updateUser(UpdateUserRequestDto userDto, String userId);

    //delete user
    void deleteUser(String userId);

    //get user by id
    UserResponseDto getUserById(String userId);

    //get all users
    List<UserResponseDto> getAllUsers();


}