package com.ledgerlite.ledgerliteapp.owner;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateOwnerRequest(

        @NotBlank
        @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\- ]+$", message = "Фамилия должна содержать только буквы")
        String lastName,

        @NotBlank
        @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\- ]+$", message = "Имя должно содержать только буквы")
        String firstName,

        @Pattern(regexp = "^[A-Za-zА-Яа-яЁё\\- ]*$", message = "Отчество должно содержать только буквы")
        String middleName,

        @NotBlank
        @Email(message = "Некорректный формат email")
        String email
) {
}
