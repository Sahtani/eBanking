package com.youcode.ebanking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserUpdateRoleDTO(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Role name is required")
        @Pattern(regexp = "ROLE_USER|ROLE_ADMIN", message = "Invalid role")
        String roleName) {
}