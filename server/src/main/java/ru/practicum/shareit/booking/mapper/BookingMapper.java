package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;


@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingDto toBookingDto(Booking booking);

    List<BookingDto> toBookingDtoList(List<Booking> bookings);

}
