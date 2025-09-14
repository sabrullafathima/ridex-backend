package com.project.ridex_backend.dto;

import com.project.ridex_backend.entity.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Builder
@Getter
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private UserRole role;
}
