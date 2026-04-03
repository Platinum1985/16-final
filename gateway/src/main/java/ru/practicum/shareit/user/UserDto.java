package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"email"})
public class UserDto {
    @NotBlank(message = "Имя не должно быть пустым")
    private String name;

    @Email(message = "Email должен быть корректным")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}


