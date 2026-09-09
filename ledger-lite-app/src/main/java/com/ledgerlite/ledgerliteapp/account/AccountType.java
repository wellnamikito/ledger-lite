package com.ledgerlite.ledgerliteapp.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "account_types")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountType {


    @Id
    @Column(name = "id", nullable = false)
    private Short id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "description", nullable = false)
    private String description;

}