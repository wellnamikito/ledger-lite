package com.ledgerlite.ledgerliteapp.account.service;

import com.ledgerlite.ledgerliteapp.account.AccountDto;
import com.ledgerlite.ledgerliteapp.account.AccountMapper;
import com.ledgerlite.ledgerliteapp.account.CreateAccountRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.UUID;


public interface AccountService {

    AccountDto createAccount(CreateAccountRequest request);

    AccountDto getAccountById(UUID id);

    Page<AccountDto> getAllAccounts(Pageable pageable);

    Page<AccountDto> getAllAccountsByOwner(UUID ownerId, Pageable pageable);

    AccountDto deposit(UUID accountId, BigDecimal amount);
}
