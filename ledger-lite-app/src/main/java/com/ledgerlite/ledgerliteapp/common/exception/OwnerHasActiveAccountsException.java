package com.ledgerlite.ledgerliteapp.common.exception;

import java.util.UUID;

public class OwnerHasActiveAccountsException extends RuntimeException {
    public OwnerHasActiveAccountsException(UUID ownerId) {
        super("Cannot delete owner with active accounts: " + ownerId);
    }
}
