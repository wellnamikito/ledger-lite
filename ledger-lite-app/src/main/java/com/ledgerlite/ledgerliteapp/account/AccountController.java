package com.ledgerlite.ledgerliteapp.account;

import com.ledgerlite.ledgerliteapp.account.service.AccountService;
import com.ledgerlite.ledgerliteapp.transfer.TransferDto;
import com.ledgerlite.ledgerliteapp.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ){
        AccountDto created = accountService.createAccount(request);
        URI loacation = URI.create("/owners/" + created.id());
        return ResponseEntity.created(loacation).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(
            @PathVariable UUID id
    ){
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping
    public ResponseEntity<Page<AccountDto>> getAllAccounts(
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable
    ){
        return ResponseEntity.ok(accountService.getAllAccounts(pageable));
    }

    @GetMapping("/by-owner/{ownerId}")
    public ResponseEntity<Page<AccountDto>> getAllAccountsByOwner(
            @PathVariable UUID ownerId,
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable
    ){
        return ResponseEntity.ok(accountService.getAllAccountsByOwner(ownerId, pageable));
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<AccountDto> deposit(
            @PathVariable("id") UUID accountId,
            @Valid @RequestBody DepositRequest request
    ){
        return ResponseEntity.ok(accountService.deposit(accountId, request.amount()));
    }

    @GetMapping("/{id}/transfers")
    public ResponseEntity<Page<TransferDto>> getAccountTransfers(
            @PathVariable UUID id,
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable
    ){
        return ResponseEntity.ok(
                transferService.getTransfersByAccountId(
                        id,
                        pageable
                )
        );
    }

}

