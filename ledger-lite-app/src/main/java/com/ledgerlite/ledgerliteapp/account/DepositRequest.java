package com.ledgerlite.ledgerliteapp.account;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO for {@link Account}
 */
public record DepositRequest(
        @NotNull(message = "amount обязателен")
        @Positive BigDecimal amount
) {
}