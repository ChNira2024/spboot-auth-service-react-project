package com.spboot.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequestDto {
    private String name;
    private String password;
    private String image;
    private Boolean enabled;
}
