package com.ledgerlite.ledgerliteapp.transfer;

import com.ledgerlite.ledgerliteapp.AbstractIntegrationTest;
import com.ledgerlite.ledgerliteapp.account.AccountDto;
import com.ledgerlite.ledgerliteapp.account.CreateAccountRequest;
import com.ledgerlite.ledgerliteapp.account.DepositRequest;
import com.ledgerlite.ledgerliteapp.owner.CreateOwnerRequest;
import com.ledgerlite.ledgerliteapp.owner.OwnerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class TransferConcurrencyIT extends AbstractIntegrationTest {

    private static final int THREAD_COUNT = 20;
    private static final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(1000);
    private static final BigDecimal TRANSFER_AMOUNT = BigDecimal.valueOf(100);
    // 20 попыток по 100 при балансе 1000 -> ровно 10 должны пройти, 10 упасть с insufficient funds.
    // Если бы не было блокировки, суммарное списание могло бы превысить баланс (гонка read-modify-write).

    private UUID createOwner(String emailPrefix) {
        String email = emailPrefix + "-" + UUID.randomUUID() + "@example.com";
        CreateOwnerRequest request = new CreateOwnerRequest("Иванов", "Иван", "Иванович", email);
        ResponseEntity<OwnerDTO> response = restTemplate.postForEntity(baseUrl("/owners"), request, OwnerDTO.class);
        return response.getBody().id();
    }

    private UUID createAccount(UUID ownerId) {
        CreateAccountRequest request = new CreateAccountRequest(ownerId, (short) 1);
        ResponseEntity<AccountDto> response = restTemplate.postForEntity(baseUrl("/accounts"), request, AccountDto.class);
        return response.getBody().id();
    }

    private void deposit(UUID accountId, BigDecimal amount) {
        restTemplate.postForEntity(baseUrl("/accounts/" + accountId + "/deposit"), new DepositRequest(amount), AccountDto.class);
    }

    private BigDecimal getBalance(UUID accountId) {
        ResponseEntity<AccountDto> response = restTemplate.getForEntity(baseUrl("/accounts/" + accountId), AccountDto.class);
        return response.getBody().balance();
    }

    private HttpStatus attemptTransfer(UUID fromAccountId, UUID toAccountId) {
        CreateTransferRequest request = new CreateTransferRequest(fromAccountId, toAccountId, TRANSFER_AMOUNT);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Idempotency-Key", UUID.randomUUID().toString());
        HttpEntity<CreateTransferRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl("/transfers"), HttpMethod.POST, entity, String.class
        );
        return (HttpStatus) response.getStatusCode();
    }

    @Test
    void concurrentTransfers_shouldNeverAllowNegativeBalanceOrDoubleCharge() throws InterruptedException {
        UUID owner1 = createOwner("payer");
        UUID owner2 = createOwner("payee");
        UUID fromAccount = createAccount(owner1);
        UUID toAccount = createAccount(owner2);
        deposit(fromAccount, INITIAL_BALANCE);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger insufficientFundsCount = new AtomicInteger();
        AtomicInteger unexpectedCount = new AtomicInteger();

        List<Future<?>> futures = IntStream.range(0, THREAD_COUNT)
                .mapToObj(i -> executor.submit(() -> {
                    try {
                        startLatch.await();
                        HttpStatus status = attemptTransfer(fromAccount, toAccount);
                        if (status == HttpStatus.CREATED) {
                            successCount.incrementAndGet();
                        } else if (status == HttpStatus.CONFLICT) {
                            insufficientFundsCount.incrementAndGet();
                        } else {
                            unexpectedCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        unexpectedCount.incrementAndGet();
                    } finally {
                        doneLatch.countDown();
                    }
                }))
                .collect(Collectors.toList());

        startLatch.countDown(); // отпускаем все потоки одновременно
        boolean completed = doneLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(completed).as("все потоки должны завершиться за отведённое время").isTrue();
        assertThat(unexpectedCount.get()).as("не должно быть неожиданных статусов").isZero();

        BigDecimal finalFromBalance = getBalance(fromAccount);
        BigDecimal finalToBalance = getBalance(toAccount);
        BigDecimal expectedTransferred = TRANSFER_AMOUNT.multiply(BigDecimal.valueOf(successCount.get()));

        // Ключевые проверки конкурентности:
        assertThat(finalFromBalance).as("баланс никогда не должен уйти в минус")
                .isGreaterThanOrEqualTo(BigDecimal.ZERO);

        assertThat(finalFromBalance).as("баланс = начальный минус ровно успешные списания (без потерь/дублей)")
                .isEqualByComparingTo(INITIAL_BALANCE.subtract(expectedTransferred));

        assertThat(finalToBalance).as("получатель должен получить ровно сумму успешных переводов")
                .isEqualByComparingTo(expectedTransferred);

        assertThat(successCount.get() + insufficientFundsCount.get())
                .as("каждый запрос должен либо пройти, либо упасть по insufficient funds")
                .isEqualTo(THREAD_COUNT);

        // При балансе 1000 и сумме перевода 100 — ровно 10 должны пройти успешно
        assertThat(successCount.get()).isEqualTo(10);
    }
}