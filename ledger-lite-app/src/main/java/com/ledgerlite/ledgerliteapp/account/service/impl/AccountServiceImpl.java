package com.ledgerlite.ledgerliteapp.account.service.impl;

import com.ledgerlite.ledgerliteapp.account.*;
import com.ledgerlite.ledgerliteapp.account.service.AccountService;
import com.ledgerlite.ledgerliteapp.common.exception.AccountNotFoundException;
import com.ledgerlite.ledgerliteapp.common.exception.AccountTypeNotFoundException;
import com.ledgerlite.ledgerliteapp.common.exception.OwnerNotFoundException;
import com.ledgerlite.ledgerliteapp.owner.Owner;
import com.ledgerlite.ledgerliteapp.owner.OwnerRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    private final OwnerRepository ownerRepository;

    private final AccountMapper accountMapper;

    private final AccountTypeRepository accountTypeRepository;

    @Override
    @Transactional
    public AccountDto createAccount(CreateAccountRequest request) {
        Owner owner = ownerRepository.findById(request.ownerId())
                .orElseThrow(() ->
                    new OwnerNotFoundException(request.ownerId())
                );

        AccountType accountType = accountTypeRepository.findById(request.accountTypeId())
                .orElseThrow(() ->
                        new AccountTypeNotFoundException(request.accountTypeId()));

        Account account = new Account();
        account.setId(UUID.randomUUID());
        account.setOwner(owner);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedAt(OffsetDateTime.now());

        Account saved = accountRepository.save(account);

        return accountMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(UUID id) {
        Account account = findAccountOrThrow(id);
        return accountMapper.toDto(account);
    }



    @Override
    @Transactional(readOnly = true)
    public Page<AccountDto> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(accountMapper::toDto);
    }

    @Override
    @Transactional
    public Page<AccountDto> getAllAccountsByOwner(UUID ownerId, Pageable pageable) {

        if(!ownerRepository.existsById(ownerId)){
            throw new OwnerNotFoundException(ownerId);
        }

        return accountRepository.findByOwnerId(ownerId, pageable)
                .map(accountMapper::toDto);
    }

    @Override
    @Transactional
    public AccountDto deposit(UUID accountId, BigDecimal amount) {

        if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Депозит цены отрицательный");
        }

        int updated = accountRepository.credit(accountId, amount);
        if (updated == 0){ throw new AccountNotFoundException(accountId);}

        Account account = findAccountOrThrow(accountId);
        return accountMapper.toDto(account);
    }

    private Account findAccountOrThrow(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException(id)
                );
    }
}
