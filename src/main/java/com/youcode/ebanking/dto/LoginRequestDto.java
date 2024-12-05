package com.youcode.ebanking.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(@NotNull String username,
        @NotNull String password) {
}
