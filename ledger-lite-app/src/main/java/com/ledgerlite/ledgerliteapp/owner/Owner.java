package com.ledgerlite.ledgerliteapp.owner;

import jakarta.persistence.*;
import lombok.*;


import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "owners")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Owner {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}