package com.ledgerlite.ledgerliteapp.transfer;

import com.ledgerlite.ledgerliteapp.AbstractIntegrationTest;
import com.ledgerlite.ledgerliteapp.account.AccountDto;
import com.ledgerlite.ledgerliteapp.account.CreateAccountRequest;
import com.ledgerlite.ledgerliteapp.account.DepositRequest;
import com.ledgerlite.ledgerliteapp.owner.CreateOwnerRequest;
import com.ledgerlite.ledgerliteapp.owner.OwnerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransferControllerIT extends AbstractIntegrationTest {

    private UUID createOwner(String emailPrefix) {
        String email = emailPrefix + "-" + UUID.randomUUID() + "@example.com";
        CreateOwnerRequest request = new CreateOwnerRequest(
                "Иванов", "Иван", "Иванович", email
        );
        ResponseEntity<OwnerDTO> response = restTemplate.postForEntity(
                baseUrl("/owners"), request, OwnerDTO.class
        );
        return response.getBody().id();
    }

    private UUID createAccount(UUID ownerId) {
        CreateAccountRequest request = new CreateAccountRequest(ownerId, (short) 1);
        ResponseEntity<AccountDto> response = restTemplate.postForEntity(
                baseUrl("/accounts"), request, AccountDto.class
        );
        return response.getBody().id();
    }

    private void deposit(UUID accountId, BigDecimal amount) {
        DepositRequest request = new DepositRequest(amount);
        restTemplate.postForEntity(
                baseUrl("/accounts/" + accountId + "/deposit"), request, AccountDto.class
        );
    }

    private BigDecimal getBalance(UUID accountId) {
        ResponseEntity<AccountDto> response = restTemplate.getForEntity(
                baseUrl("/accounts/" + accountId), AccountDto.class
        );
        return response.getBody().balance();
    }

    private ResponseEntity<TransferDto> postTransfer(
            UUID fromAccountId, UUID toAccountId, BigDecimal amount, UUID idempotencyKey
    ) {
        CreateTransferRequest request = new CreateTransferRequest(fromAccountId, toAccountId, amount);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", idempotencyKey.toString());
        HttpEntity<CreateTransferRequest> entity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(
                baseUrl("/transfers"), HttpMethod.POST, entity, TransferDto.class
        );
    }

    private ResponseEntity<String> postTransferRaw(
            UUID fromAccountId, UUID toAccountId, BigDecimal amount, UUID idempotencyKey
    ) {
        CreateTransferRequest request = new CreateTransferRequest(fromAccountId, toAccountId, amount);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", idempotencyKey.toString());
        HttpEntity<CreateTransferRequest> entity = new HttpEntity<>(request, headers);
        return restTemplate.exchange(
                baseUrl("/transfers"), HttpMethod.POST, entity, String.class
        );
    }

    @Test
    void transfer_shouldMoveBalanceBetweenAccounts() {
        UUID owner1 = createOwner("payer");
        UUID owner2 = createOwner("payee");
        UUID fromAccount = createAccount(owner1);
        UUID toAccount = createAccount(owner2);
        deposit(fromAccount, BigDecimal.valueOf(100));

        ResponseEntity<TransferDto> response = postTransfer(
                fromAccount, toAccount, BigDecimal.valueOf(30), UUID.randomUUID()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().status()).isEqualTo(TransferStatus.COMPLETED);
        assertThat(getBalance(fromAccount)).isEqualByComparingTo(BigDecimal.valueOf(70));
        assertThat(getBalance(toAccount)).isEqualByComparingTo(BigDecimal.valueOf(30));
    }

    @Test
    void transfer_withSameIdempotencyKey_shouldNotDoubleCharge() {
        UUID owner1 = createOwner("payer");
        UUID owner2 = createOwner("payee");
        UUID fromAccount = createAccount(owner1);
        UUID toAccount = createAccount(owner2);
        deposit(fromAccount, BigDecimal.valueOf(100));
        UUID idempotencyKey = UUID.randomUUID();

        postTransfer(fromAccount, toAccount, BigDecimal.valueOf(30), idempotencyKey);
        postTransfer(fromAccount, toAccount, BigDecimal.valueOf(30), idempotencyKey);

        assertThat(getBalance(fromAccount)).isEqualByComparingTo(BigDecimal.valueOf(70));
        assertThat(getBalance(toAccount)).isEqualByComparingTo(BigDecimal.valueOf(30));
    }

    @Test
    void transfer_withInsufficientFunds_shouldReturn409() {
        UUID owner1 = createOwner("payer");
        UUID owner2 = createOwner("payee");
        UUID fromAccount = createAccount(owner1);
        UUID toAccount = createAccount(owner2);

        ResponseEntity<String> response = postTransferRaw(
                fromAccount, toAccount, BigDecimal.valueOf(50), UUID.randomUUID()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void transfer_toSameAccount_shouldReturn400() {
        UUID owner = createOwner("selftransfer");
        UUID account = createAccount(owner);
        deposit(account, BigDecimal.valueOf(100));

        ResponseEntity<String> response = postTransferRaw(
                account, account, BigDecimal.valueOf(10), UUID.randomUUID()
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getTransferById_whenNotExists_shouldReturn404() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl("/transfers/999999"), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}