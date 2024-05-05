package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class IndicatorBooking {

    private final long id;

    private final long bookerId;

}
