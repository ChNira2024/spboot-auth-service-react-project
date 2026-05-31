package com.spboot.auth.dto;

import com.spboot.auth.entity.Role;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

    private UUID id;
    private String email;
    private String name;
    private boolean enabled;
    private Instant createdAt;
    private String image;
    private Set<Role> roles;

}