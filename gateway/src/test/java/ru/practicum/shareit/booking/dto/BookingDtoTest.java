package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

    @Autowired
    private JacksonTester<CreateBooking> jacksonTesterCreateBooking;

    List<BookingDto> bookingList;

    List<UserDto> userDtoList;

    List<ItemDto> itemDtoList;

    List<CreateBooking> createBookingList;

    LocalDateTime start;
    LocalDateTime end;

    @BeforeEach
    void start() {
        userDtoList = Data.<UserDto>generationData(1, UserDto.class);
        itemDtoList = Data.<ItemDto>generationData(1, ItemDto.class, userDtoList.get(0), 1L);
        bookingList = Data.<BookingDto>generationData(1, BookingDto.class, userDtoList.get(0), itemDtoList.get(0));
        createBookingList = Data.<CreateBooking>generationData(1, CreateBooking.class, 1);
        start = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
        end = LocalDateTime.of(2024, 1, 1, 1, 1, 1);
    }

    @Test
    void testBookingDto() throws Exception {

        BookingDto bookingDto = bookingList.get(0).toBuilder()
                .start(start)
                .end(end)
                .build();

        ItemDto item = bookingDto.getItem();

        UserDto booker = bookingDto.getBooker();

        JsonContent<BookingDto> result = jacksonTester.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("описание вещи 1");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("User1");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("user1@mail");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");

    }

    @Test
    void testCreateBooking() throws Exception {
        CreateBooking createBooking = new CreateBooking(1L, start, end);
        JsonContent<CreateBooking> result = jacksonTesterCreateBooking.write(createBooking);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.toString());
    }
}