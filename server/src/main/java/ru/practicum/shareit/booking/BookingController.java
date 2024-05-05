package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
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
    public BookingDto addBooking(@RequestBody @Valid CreateBooking createBooking,
                                 @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return mapper.toBookingDto(bookingService.addBooking(createBooking, userId));
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
