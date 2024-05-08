package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.excteption.BadRequestException;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addBooking(CreateBooking createBooking, Long userId) {
        validDate(createBooking);
        return post("", userId, createBooking);
    }

    public ResponseEntity<Object> setStatus(Long bookingId, Long userId, Boolean approved) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId,
                "approved", approved
        );
        return patch("/{bookingId}?approved={approved}",userId,parameters);
    }

    public ResponseEntity<Object> getBookingByIdForUserId(Long bookingId, Long userId) {
        Map<String, Object> parameters = Map.of(
                "bookingId", bookingId
        );
        return get("/{bookingId}",userId,parameters);
    }

    public ResponseEntity<Object> getBookingsForBooker(Long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/?state={state}&from={from}&size={size}",userId,parameters);
    }

    public ResponseEntity<Object> getBookingsForOwner(Long userId, BookingState state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "from", from,
                "size", size
        );
        return get("/owner?state={state}&from={from}&size={size}",userId,parameters);
    }

        private void validDate(CreateBooking createBooking) {
        LocalDateTime start = createBooking.getStart();
        LocalDateTime end = createBooking.getEnd();

        int equals = start.compareTo(end);

        if (equals == 0) {
            throw new BadRequestException(
                    String.format("Время начала %s бронирования не может быть равно времени окончания %s",start,end)
            );
        }

        if (equals > 0) {
            throw new BadRequestException(
                    String.format("Время начала %s бронирования не может быть позже времени окончания %s",start,end)
            );
        }

        if (start.compareTo(LocalDateTime.now()) < 0) {
            throw new BadRequestException( String.format("Время начала %s бронирования не может быть в прошлом!",start));
        }
    }
}
