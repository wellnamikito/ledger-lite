package com.ledgerlite.ledgerliteapp.common.exception;

import java.util.UUID;

public class AccountTypeNotFoundException extends RuntimeException {
    public AccountTypeNotFoundException(Short accountTypeId) {
        super("Тип аккаунта не айден: "+ accountTypeId );
    }
}
