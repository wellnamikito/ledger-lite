package com.ledgerlite.ledgerliteapp.common.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID accountId) {
        super("Аккаунт не найден: " + accountId);
    }
}
