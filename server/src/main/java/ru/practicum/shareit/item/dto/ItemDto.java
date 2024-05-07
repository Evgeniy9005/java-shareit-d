package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.booking.IndicatorBooking;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder(toBuilder = true)
public class ItemDto {

    private final Long id;

    private final String name;

    private final String description;

    private final Boolean available;

    private final UserDto owner;

    private final Long requestId;

    private final IndicatorBooking lastBooking;

    private final IndicatorBooking nextBooking;

    @Builder.Default
    private final List<CommentDto> comments = new ArrayList<>();

}
