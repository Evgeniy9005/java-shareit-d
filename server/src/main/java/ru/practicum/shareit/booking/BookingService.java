package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.CreateBooking;

import java.util.List;

public interface BookingService {

    Booking addBooking(CreateBooking createBooking, Long userId);

    Booking setStatus(long bookingId, long userId, Boolean approved);

    Booking getBookingByIdForUserId(long bookingId, long userId);

    List<Booking> getBookingsForBooker(long userId, String state, int from, int size);

    List<Booking> getBookingsOwnerState(long userId, String state, int from, int size);
}
