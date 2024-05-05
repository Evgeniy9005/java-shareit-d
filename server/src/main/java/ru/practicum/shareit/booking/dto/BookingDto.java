package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingDto {

    private final long id;

    private final LocalDateTime start;

    private final LocalDateTime end;

    private final Item item;

    private final User booker;

    private final String status;

}
