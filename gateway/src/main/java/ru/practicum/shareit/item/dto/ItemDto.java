package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemDto {
    private final Long id;

    @NotBlank
    private final String name;

    @NotBlank
    private final String description;

    @NotNull
    private final Boolean available;

   // private final User owner;

    private final Long requestId;

    private final IndicatorBooking lastBooking;

    private final IndicatorBooking nextBooking;

    @Builder.Default
    private final List<CommentDto> comments = new ArrayList<>();

}
