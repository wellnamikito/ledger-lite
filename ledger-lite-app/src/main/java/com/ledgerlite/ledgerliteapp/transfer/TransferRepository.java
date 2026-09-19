package com.ledgerlite.ledgerliteapp.transfer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByIdempotencyKey(UUID idempotencyKey);

    @Query("""
    SELECT t FROM Transfer t
        WHERE t.fromAccount.id = :accountId
        OR t.toAccount.id = :accountId
        ORDER BY t.createdAt DESC
    """)
    Page<Transfer> findByAccountId(
            @Param("accountId")
            UUID accountId,
            Pageable pageable
    );
}