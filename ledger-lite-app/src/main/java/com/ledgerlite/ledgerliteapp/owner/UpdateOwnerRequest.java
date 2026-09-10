package com.ledgerlite.ledgerliteapp.owner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for {@link Owner}
 */
public record UpdateOwnerRequest(

        @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\- ]+$")
        @NotBlank String lastName,

        @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\- ]+$")
        @NotBlank String firstName,

        @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\- ]+$")
        String middleName) {
}