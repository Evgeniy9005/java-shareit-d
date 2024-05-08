package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.*;
import ru.practicum.shareit.user.dto.UserDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.data.Data.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingMapper bookingMapperMock;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private List<User> userList;

    private List<UserDto> userDtoList;

    private List<Item> itemList;

    private List<Booking> bookingList;

    private List<BookingDto> bookingDtoList;

    private UserMapper userMapper = new UserMapperImpl();

    private BookingMapper bookingMapper = new BookingMapperImpl();

    Booking booking;
    BookingDto bookingDto;

    private LocalDateTime start = LocalDateTime.of(2024,1,1,1,1,1,1);
    private LocalDateTime end = LocalDateTime.of(2024,1,1,1,1,2,1);



    @BeforeEach
    void start() {


        userList = generationData(2, User.class);
        printList(userList,">>>");

        userDtoList = userList.stream().map(user -> userMapper.toUserDto(user)).collect(Collectors.toList());
        printList(userDtoList,"<<<");

        itemList = generationData(2, Item.class, userList.get(0),1L);
        printList(itemList,">|<");

        bookingList = generationData(2,Booking.class,userList.get(0),itemList.get(0));
        printList(bookingList,"xxx");

        booking = bookingList.get(0).toBuilder().start(start).end(end).build();

        bookingDto = bookingMapper.toBookingDto(booking);

        bookingDtoList = bookingMapper.toBookingDtoList(bookingList.stream()
                .map(booking1 -> booking1.toBuilder()
                        .start(start)
                        .end(end)
                        .build())
                .collect(Collectors.toList()));
    }

    @Test
    void addBooking() throws Exception {

/*
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
                .andExpect(status().isBadRequest());*/

        when(bookingMapperMock.toBookingDto(any(Booking.class))).thenReturn(bookingDto);
        when(bookingService.addBooking(any(CreateBooking.class),anyLong())).thenReturn(booking);

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

        when(bookingMapperMock.toBookingDto(any(Booking.class))).thenReturn(bookingDto);
        when(bookingService.setStatus(anyLong(),anyLong(),anyBoolean())).thenReturn(booking);

        mvc.perform(patch("/bookings/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("approved","true")
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
    void getBookingByIdForUserId() throws Exception {
        when(bookingMapperMock.toBookingDto(any(Booking.class))).thenReturn(bookingDto);
        when(bookingService.getBookingByIdForUserId(anyLong(),anyLong())).thenReturn(booking);

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
        when(bookingMapperMock.toBookingDtoList(any())).thenReturn(bookingDtoList);
        when(bookingService.getBookingsForBooker(anyLong(),anyString(),any(Integer.class),any(Integer.class)))
                .thenReturn(bookingList);

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
        when(bookingMapperMock.toBookingDtoList(any())).thenReturn(bookingDtoList);
        when(bookingService.getBookingsOwnerState(anyLong(),anyString(),any(Integer.class),any(Integer.class)))
                .thenReturn(bookingList);

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