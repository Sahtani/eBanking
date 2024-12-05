package com.youcode.ebanking.dto;

import jakarta.validation.constraints.NotBlank;

public record RoleEmbeddableDto(
        @NotBlank(message = "Role name is required")
        String name
) {}
