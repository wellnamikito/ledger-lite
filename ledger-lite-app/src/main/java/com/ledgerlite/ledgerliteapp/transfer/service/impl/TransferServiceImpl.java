package com.ledgerlite.ledgerliteapp.transfer.service.impl;

import com.ledgerlite.ledgerliteapp.account.Account;
import com.ledgerlite.ledgerliteapp.account.AccountRepository;
import com.ledgerlite.ledgerliteapp.common.exception.AccountNotFoundException;
import com.ledgerlite.ledgerliteapp.common.exception.InsufficientFundsException;
import com.ledgerlite.ledgerliteapp.common.exception.TransferNotFoundException;
import com.ledgerlite.ledgerliteapp.transfer.*;
import com.ledgerlite.ledgerliteapp.transfer.service.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final TransferMapper transferMapper;

    @Override
    @Transactional
    public TransferDto transfer(CreateTransferRequest request, UUID idempotencyKey) {
        // 1. Идемпотентность: если такой улюч обрабатывается - вернуть тот же результат
        var existing = transferRepository.findByIdempotencyKey(idempotencyKey);
        if(existing.isPresent()) return transferMapper.toDto(existing.get());

        UUID fromId = request.fromAccountId();
        UUID toId = request.toAccountId();

        if(fromId.equals(toId)) throw new IllegalArgumentException("fromAccountId and toAccountId must differ");

        // 2. Фиксированный порядок блокировки - по возрастанию UUID
        UUID firstId = fromId.compareTo(toId) < 0 ? fromId : toId;
        UUID secondId = fromId.compareTo(toId) < 0 ? toId : fromId;

        Account first = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new AccountNotFoundException(firstId));
        Account second = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new AccountNotFoundException(secondId));

        // 3. Определяем, кто из заблокированных - from, кто - to
        Account fromAccount = fromId.equals(firstId) ? first : second;
        Account toAccount   = fromId.equals(firstId) ? second : first;

        Transfer transferRecord = new Transfer();
        transferRecord.setIdempotencyKey(idempotencyKey);
        transferRecord.setFromAccount(fromAccount);
        transferRecord.setToAccount(toAccount);
        transferRecord.setAmount(request.amount());

        if(fromAccount.getBalance().compareTo(request.amount()) < 0){
            transferRecord.setStatus(TransferStatus.FAILED);
            transferRepository.save(transferRecord);
            throw new InsufficientFundsException(fromId);
        }

        // Обе строки уже под блокировкой - можно менять баланс напрямую через entity
        fromAccount.setBalance(fromAccount.getBalance().subtract(request.amount()));
        toAccount.setBalance(toAccount.getBalance().add(request.amount()));

        transferRecord.setStatus(TransferStatus.COMPLETED);
        Transfer saved = transferRepository.save(transferRecord);

        return transferMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TransferDto getById(Long id){
        Transfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new TransferNotFoundException(id));
        return transferMapper.toDto(transfer);
    }
}
