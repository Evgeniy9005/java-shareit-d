package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    private BookingClient bookingClient;

    RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();

    TestRestTemplate testRestTemplate;

    private List<BookingDto> bookingList;

    private List<ItemDto> itemList;

    private List<UserDto> userList;

    @BeforeEach
    void start() {

        restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory( "http://localhost:9090/bookings"))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();

        testRestTemplate = new TestRestTemplate(restTemplateBuilder);
/*
        bookingClient = new BookingClient("",
                testRestTemplate
        );*/

       /* userList = Data.generationData(2,User.class);
        Data.printList(userList,">>>");

        itemList = Data.generationData(2,Item.class,userList.get(0),1L);
        Data.printList(itemList,"===");

        bookingList = Data.generationData(8,Booking.class,userList.get(0),itemList.get(0));
        bookingList.sort(bookingSortDescById);
        Data.printList(bookingList,"_+_");*/
    }


    /*@Test
    void validFromSize() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        assertThrows(BadRequestException.class, () -> bookingService.getBookingsOwnerState(1, Data.ALL,0,0));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsOwnerState(1, Data.ALL,-1,10));
        assertThrows(BadRequestException.class, () -> bookingService.getBookingsOwnerState(1, Data.ALL,1,-10));
    }*/

  /*  @Test
    void addBookingError() {
        assertThrows(BadRequestException.class,() -> bookingService.addBooking(
                        new CreateBooking(1L,START_DATE,START_DATE),1L),
                "Время начала 2024-01-01T01:01 бронирования не может быть " +
                        "равно времени окончания 2024-01-01T01:01");
    }*/

   /* @Test
    void addBookingError1() {
        assertThrows(BadRequestException.class,() -> bookingService.addBooking(
                        new CreateBooking(1L,LocalDateTime.now().plusDays(1),LocalDateTime.now()),1L),
                "Время начала 2024-03-27T08:05:26.121644 бронирования не может быть " +
                        "позже времени окончания 2024-03-26T08:05:26.121644");
    }*/

    @Test
    void addBooking() {
       /* ResponseEntity<Object> response = bookingClient.addBooking(
                new CreateBooking(1L, LocalDateTime.now().plusMinutes(1),LocalDateTime.now().plusDays(1)),
                1L);*/
    }

    @Test
    void setStatus() {
    }

    @Test
    void getBookingByIdForUserId() {
    }

    @Test
    void getBookingsForBooker() {
    }

    @Test
    void getBookingsForOwner() {
    }
}