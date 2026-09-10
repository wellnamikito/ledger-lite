package com.ledgerlite.ledgerliteapp.common.exception;

import java.util.UUID;

public class OwnerNotFoundException extends RuntimeException {
    public OwnerNotFoundException(UUID ownerId) {
        super("Владелец не найден: " + ownerId);
    }
}
