package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.data.Data.*;
import static ru.practicum.shareit.data.Data.ALL;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    private List<UserDto> userDtoList;

    private List<ItemDto> itemDtoList;

    private List<BookingDto> bookingDtoList;

    BookingDto bookingDto;

    private LocalDateTime start = LocalDateTime.of(2024,1,1,1,1,1,1);
    private LocalDateTime end = LocalDateTime.of(2024,1,1,1,1,2,1);



    @BeforeEach
    void start() {

        userDtoList = generationData(2, UserDto.class);
        printList(userDtoList,">>>");

        itemDtoList = generationData(2, ItemDto.class, userDtoList.get(0),1L);
        printList(itemDtoList,">|<");

        bookingDtoList = generationData(2,BookingDto.class,userDtoList.get(0), itemDtoList.get(0));
        printList(bookingDtoList,"xxx");

        bookingDto = bookingDtoList.get(0).toBuilder().start(start).end(end).build();

        bookingDtoList = bookingDtoList.stream()
                .map(booking1 -> booking1.toBuilder()
                        .start(start)
                        .end(end)
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    void addBooking() throws Exception {
        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(new CreateBooking(null,start,end)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());


        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(new CreateBooking(1L,null,end)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(new CreateBooking(1L,start,null)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        ResponseEntity<Object> bookingDtoResponseEntity = new ResponseEntity<>(bookingDto, HttpStatus.OK);
        when(bookingClient.addBooking(any(CreateBooking.class),anyLong())).thenReturn(bookingDtoResponseEntity);

        mvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(new CreateBooking(1L,start,end)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.start",is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end",is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.booker.id",is(1)))
                .andExpect(jsonPath("$.item.id",is(1)))
                .andExpect(jsonPath("$.status",is(bookingDto.getStatus())));
    }

    @Test
    void setStatus() throws Exception {

        ResponseEntity<Object> bookingDtoResponseEntity = new ResponseEntity<>(bookingDto, HttpStatus.OK);
        when(bookingClient.setStatus(anyLong(),anyLong(),anyBoolean())).thenReturn(bookingDtoResponseEntity);

        mvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("approved","true")
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.start",is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end",is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.booker.id",is(1)))
                .andExpect(jsonPath("$.item.id",is(1)))
                .andExpect(jsonPath("$.status",is(bookingDto.getStatus())));

    }

    @Test
    void getBookingByIdForUserId() throws Exception {
        ResponseEntity<Object> bookingDtoResponseEntity = new ResponseEntity<>(bookingDto, HttpStatus.OK);
        when(bookingClient.getBookingByIdForUserId(anyLong(),anyLong())).thenReturn(bookingDtoResponseEntity);

        mvc.perform(get("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.start",is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end",is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.booker.id",is(1)))
                .andExpect(jsonPath("$.item.id",is(1)))
                .andExpect(jsonPath("$.status",is(bookingDto.getStatus())));
    }

    @Test
    void getBookingsForBooker() throws Exception {
        ResponseEntity<Object> bookingDtoResponseEntity = new ResponseEntity<>(bookingDtoList, HttpStatus.OK);
        when(bookingClient.getBookingsForBooker(anyLong(),any(),any(Integer.class),any(Integer.class)))
                .thenReturn(bookingDtoResponseEntity);

        BookingDto bookingDto1 = bookingDtoList.get(0);
        BookingDto bookingDto2 = bookingDtoList.get(1);

        mvc.perform(get("/bookings")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .param("state", ALL)
                        .param("form","0")
                        .param("size","10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",is(1),Integer.class))
                .andExpect(jsonPath("$[0].start",is(bookingDto1.getStart().toString())))
                .andExpect(jsonPath("$[0].end",is(bookingDto1.getEnd().toString())))
                .andExpect(jsonPath("$[0].booker.id",is(1)))
                .andExpect(jsonPath("$[0].item.id",is(1)))
                .andExpect(jsonPath("$[0].status",is(bookingDto1.getStatus())))
                .andExpect(jsonPath("$[1].id",is(2),Integer.class))
                .andExpect(jsonPath("$[1].start",is(bookingDto2.getStart().toString())))
                .andExpect(jsonPath("$[1].end",is(bookingDto2.getEnd().toString())))
                .andExpect(jsonPath("$[1].booker.id",is(1)))
                .andExpect(jsonPath("$[1].item.id",is(1)))
                .andExpect(jsonPath("$[1].status",is(bookingDto2.getStatus())));
    }

    @Test
    void getBookingsForOwner() throws Exception {

        ResponseEntity<Object> bookingDtoResponseEntity = new ResponseEntity<>(bookingDtoList, HttpStatus.OK);
        when(bookingClient.getBookingsForOwner(anyLong(),any(),any(Integer.class),any(Integer.class)))
                .thenReturn(bookingDtoResponseEntity);

        BookingDto bookingDto1 = bookingDtoList.get(0);
        BookingDto bookingDto2 = bookingDtoList.get(1);

        mvc.perform(get("/bookings/owner")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .param("state", ALL)
                        .param("form","0")
                        .param("size","10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",is(1),Integer.class))
                .andExpect(jsonPath("$[0].start",is(bookingDto1.getStart().toString())))
                .andExpect(jsonPath("$[0].end",is(bookingDto1.getEnd().toString())))
                .andExpect(jsonPath("$[0].booker.id",is(1)))
                .andExpect(jsonPath("$[0].item.id",is(1)))
                .andExpect(jsonPath("$[0].status",is(bookingDto1.getStatus())))
                .andExpect(jsonPath("$[1].id",is(2),Integer.class))
                .andExpect(jsonPath("$[1].start",is(bookingDto2.getStart().toString())))
                .andExpect(jsonPath("$[1].end",is(bookingDto2.getEnd().toString())))
                .andExpect(jsonPath("$[1].booker.id",is(1)))
                .andExpect(jsonPath("$[1].item.id",is(1)))
                .andExpect(jsonPath("$[1].status",is(bookingDto2.getStatus())));
    }
}