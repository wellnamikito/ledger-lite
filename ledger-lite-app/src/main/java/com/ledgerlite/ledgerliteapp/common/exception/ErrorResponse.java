package com.ledgerlite.ledgerliteapp.common.exception;

import java.time.OffsetDateTime;

public record ErrorResponse(
        int status,
        String message,
        OffsetDateTime timestamp
) {
}
