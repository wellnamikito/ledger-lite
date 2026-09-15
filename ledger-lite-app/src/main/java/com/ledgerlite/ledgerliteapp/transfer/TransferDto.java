package com.ledgerlite.ledgerliteapp.transfer;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for {@link Transfer}
 */
public record TransferDto(
        Long id,
        UUID idempotencyKey,
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        TransferStatus status,
        OffsetDateTime createdAt
){}