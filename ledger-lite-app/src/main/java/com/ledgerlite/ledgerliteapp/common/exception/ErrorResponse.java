package com.ledgerlite.ledgerliteapp.exception;

import java.time.OffsetDateTime;

public record ErrorResponse(
        int status,
        String message,
        OffsetDateTime timestamp
) {
}
