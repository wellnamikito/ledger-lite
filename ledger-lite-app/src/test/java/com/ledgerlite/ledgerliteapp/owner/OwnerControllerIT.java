package com.ledgerlite.ledgerliteapp.owner;

import com.ledgerlite.ledgerliteapp.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerControllerIT extends AbstractIntegrationTest {

    @Test
    void createOwner_shouldReturn210WithCreateOwner(){
        CreateOwnerRequest request = new CreateOwnerRequest(
                "Иванов", "Иван", "Иванович", "ivan.test@example.com"
        );

        ResponseEntity<OwnerDTO> response = restTemplate.postForEntity(
                baseUrl("/owners"), request, OwnerDTO.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().email()).isEqualTo("ivan.test@example.com");
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


}
