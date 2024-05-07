package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    private final BookingMapper mapper;

    @PostMapping
    public BookingDto addBooking(@RequestBody CreateBooking createBooking,
                                 @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        Booking booking =  bookingService.addBooking(createBooking, userId);
        BookingDto bookingDto = mapper.toBookingDto(booking);
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setStatus(@PathVariable Long bookingId,
                                @RequestHeader("X-Sharer-User-Id") Long userId,
                                @RequestParam Boolean approved
    ) {
        return mapper.toBookingDto(bookingService.setStatus(bookingId,userId,approved));
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingByIdForUserId(@PathVariable Long bookingId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return mapper.toBookingDto(bookingService.getBookingByIdForUserId(bookingId,userId));
    }

    @GetMapping
    public Collection<BookingDto> getBookingsForBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "default") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam (defaultValue = "10") Integer size
    ) {
        return mapper.toBookingDtoList(bookingService.getBookingsForBooker(userId,state,from,size));
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsForOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "default") String state,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return mapper.toBookingDtoList(bookingService.getBookingsOwnerState(userId, state, from, size));
    }
}
