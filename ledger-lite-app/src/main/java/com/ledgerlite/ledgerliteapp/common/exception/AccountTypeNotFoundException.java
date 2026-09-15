package com.ledgerlite.ledgerliteapp.common.exception;



public class AccountTypeNotFoundException extends RuntimeException {
    public AccountTypeNotFoundException(Short accountTypeId) {
        super("Тип аккаунта не айден: "+ accountTypeId );
    }
}
