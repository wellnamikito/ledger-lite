package com.ledgerlite.ledgerliteapp.owner;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OwnerDTO(
        UUID id,
        String lastName,
        String firstName,
        String middleName,
        String email,
        OffsetDateTime createdAt
) {
}
