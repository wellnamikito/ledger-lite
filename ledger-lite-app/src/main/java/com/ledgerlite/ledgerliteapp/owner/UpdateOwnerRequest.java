package com.ledgerlite.ledgerliteapp.owner;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for {@link Owner}
 */
public record UpdateOwnerRequest(@Pattern @NotBlank String lastName,
                                 @Pattern(regexp = "^[A-Za-z\u0410-\u042F\u0430-\u044F\u0401\u0451\\\\- ]+$") @NotBlank String firstName,
                                 @Pattern(regexp = "^[A-Za-z\u0410-\u042F\u0430-\u044F\u0401\u0451\\\\- ]+$") String middleName) {
}