package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder(toBuilder = true)
public class UserDto {
    private final Long id;

    private final String name;

    @Email
    @NotBlank
    private final String email;
}
