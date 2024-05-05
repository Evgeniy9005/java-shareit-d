package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class CreateItemRequest {

    private final int id;

    @NotBlank
    private final String description;

}
