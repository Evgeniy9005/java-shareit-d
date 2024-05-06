package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@ToString
@Builder(toBuilder = true)
@AllArgsConstructor
@EqualsAndHashCode
public class ItemRequestDto {

    private final long id;

    private final String description;

    private final LocalDateTime created;

    private final long requester;

    @Builder.Default
    private final List<ItemDto> items = new ArrayList<>();

}
