package com.youcode.ebanking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserResponseDTO(Long id,
                      @NotBlank(message = "Username is required")
                      @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
                      String username,
                      @NotBlank(message = "Email is required")
                      @Email(message = "Invalid email format")
                      String email,
                              RoleEmbeddableDto role,
                      boolean enabled) {
}
