package com.ledgerlite.ledgerliteapp.common.exception;

public class TransferNotFoundException extends RuntimeException {
    public TransferNotFoundException(Long id) {
        super("Transfer not found " + id);
    }
}
