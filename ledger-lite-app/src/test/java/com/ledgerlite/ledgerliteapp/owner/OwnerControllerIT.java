package com.ledgerlite.ledgerliteapp.owner;

import com.ledgerlite.ledgerliteapp.AbstractIntegrationTest;
import com.ledgerlite.ledgerliteapp.account.AccountDto;
import com.ledgerlite.ledgerliteapp.account.CreateAccountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerControllerIT extends AbstractIntegrationTest {

    @Test
    void createOwner_shouldReturn210WithCreateOwner(){
        String email = "ivan.test-" + UUID.randomUUID() + "@example.com";
        CreateOwnerRequest request = new CreateOwnerRequest(
                "Иванов", "Иван", "Иванович", email
        );

        ResponseEntity<OwnerDTO> response = restTemplate.postForEntity(
                baseUrl("/owners"), request, OwnerDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo(email);
        assertThat(response.getBody().id()).isNotNull();
    }

    @Test
    void getOwnerById_whenNotExists_shouldReturn404(){
        java.util.UUID randomId = java.util.UUID.randomUUID();

        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl("/owners/" + randomId), String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteOwner_withActiveAccounts_shouldReturn409(){
        // создаем owner
        CreateOwnerRequest ownerRequest = new CreateOwnerRequest(
                "Петров",
                "Петр",
                "Петрович",
                "petrov-" + UUID.randomUUID() + "@example.com"
        );

        ResponseEntity<OwnerDTO> ownerResponse = restTemplate
                .postForEntity(
                        baseUrl("/owners"),
                        ownerRequest,
                        OwnerDTO.class
                );

        UUID ownerId = ownerResponse.getBody().id();

        // создаем аккаунт для этого владельца
        CreateAccountRequest accountRequest = new CreateAccountRequest(
                ownerId,
                (short) 1
        );
        restTemplate.postForEntity(baseUrl(
                "/accounts"),
                accountRequest,
                AccountDto.class);

        // делаем удаление
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                baseUrl("/owners/" + ownerId),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void deleteOwner_withoutAccounts_shouldReturn204() {
        CreateOwnerRequest ownerRequest = new CreateOwnerRequest(
                "Сидоров", "Сидор", "Сидорович", "sidorov-" + UUID.randomUUID() + "@example.com"
        );
        ResponseEntity<OwnerDTO> ownerResponse = restTemplate.postForEntity(
                baseUrl("/owners"), ownerRequest, OwnerDTO.class
        );
        UUID ownerId = ownerResponse.getBody().id();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                baseUrl("/owners/" + ownerId), HttpMethod.DELETE, null, Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
