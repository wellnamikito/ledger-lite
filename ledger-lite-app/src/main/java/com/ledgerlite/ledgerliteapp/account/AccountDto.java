package com.ledgerlite.ledgerliteapp.account;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for {@link Account}
 */
public record AccountDto(UUID id, UUID ownerId, Short accountTypeId, BigDecimal balance, Long version,
                         OffsetDateTime createdAt) {
}