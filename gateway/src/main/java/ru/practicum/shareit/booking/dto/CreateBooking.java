package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@ToString
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class CreateBooking {

    @NotNull
    private final Long itemId;

    @FutureOrPresent
    private final LocalDateTime start;

    @Future
    private final LocalDateTime end;

}
