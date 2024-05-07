package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@ToString
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class CreateBooking {


    private final Long itemId;


    private final LocalDateTime start;


    private final LocalDateTime end;

}
