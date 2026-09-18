package com.ledgerlite.ledgerliteapp.account.service.impl;

import com.ledgerlite.ledgerliteapp.account.*;
import com.ledgerlite.ledgerliteapp.common.exception.AccountNotFoundException;
import com.ledgerlite.ledgerliteapp.owner.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountTypeRepository accountTypeRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private UUID accountId;
    private Account account;
    private AccountDto accountDto;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        account = new Account();
        account.setId(accountId);
        account.setBalance(BigDecimal.valueOf(150));
        account.setVersion(0L);
        account.setCreatedAt(OffsetDateTime.now());

        accountDto = new AccountDto(
                accountId, UUID.randomUUID(), (short) 1,
                BigDecimal.valueOf(150), 0L, account.getCreatedAt()
        );
    }

    @Test
    void deposit_withValidAmount_shouldCreditAndReturnUpdatedAccount() {
        when(accountRepository.credit(eq(accountId), eq(BigDecimal.valueOf(50)))).thenReturn(1);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.deposit(accountId, BigDecimal.valueOf(50));

        assertThat(result).isEqualTo(accountDto);
        verify(accountRepository).credit(accountId, BigDecimal.valueOf(50));
        verify(accountRepository).findById(accountId);
        verify(accountMapper).toDto(account);
    }

    @Test
    void deposit_whenAccountNotFound_shouldThrowAccountNotFoundException() {
        when(accountRepository.credit(eq(accountId), any())).thenReturn(0);

        assertThatThrownBy(() -> accountService.deposit(accountId, BigDecimal.valueOf(50)))
                .isInstanceOf(AccountNotFoundException.class);

        verify(accountRepository, never()).findById(any());
        verify(accountMapper, never()).toDto(any());
    }

    @Test
    void deposit_withNegativeAmount_shouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> accountService.deposit(accountId, BigDecimal.valueOf(-10)))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(accountRepository);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void deposit_withZeroAmount_shouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> accountService.deposit(accountId, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(accountRepository);
    }

    @Test
    void deposit_withNullAmount_shouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> accountService.deposit(accountId, null))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(accountRepository);
    }
}