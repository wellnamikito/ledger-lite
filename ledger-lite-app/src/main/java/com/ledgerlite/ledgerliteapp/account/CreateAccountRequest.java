package com.ledgerlite.ledgerliteapp.account;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO for {@link Account}
 */
public record CreateAccountRequest(

        @NotNull(message = "ownerId обязателен")
        UUID ownerId,
        @NotNull(message = "accountTypeId обязателен")
        Short accountTypeId
) {
}