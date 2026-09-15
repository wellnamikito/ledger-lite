package com.ledgerlite.ledgerliteapp.transfer.service;

import com.ledgerlite.ledgerliteapp.transfer.CreateTransferRequest;
import com.ledgerlite.ledgerliteapp.transfer.TransferDto;

import java.util.UUID;

public interface TransferService {
    TransferDto transfer(
            CreateTransferRequest request,
            UUID idempotencyKey
    );
    TransferDto getById(Long id);
}
