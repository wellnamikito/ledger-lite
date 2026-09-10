package com.ledgerlite.ledgerliteapp.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Page<Account> findByOwnerId(UUID ownerId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("""
    UPDATE Account a SET a.balance = a.balance - :amount
    WHERE a.id = :id AND a.balance >= :amount 
    """)
    int debit(
            @Param("id") UUID id,
            @Param("amount") BigDecimal amount
    );

    @Modifying(clearAutomatically = true)
    @Query("""
    UPDATE Account a SET a.balance = a.balance + :amount
    WHERE a.id = :id
    """)
    int credit(
            @Param("id") UUID id,
            @Param("amount") BigDecimal amount
    );
}