package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.CreateBooking;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.data.Data.START_DATE;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    private List<Booking> bookingList;

    private List<Item> itemList;

    private List<User> userList;

    Comparator<Booking> bookingSortDescById;
    Comparator<Booking> bookingSortDescByStart;

    @BeforeEach
    void start() {

        bookingSortDescById = (b1,b2) -> Math.toIntExact(b2.getId() - b1.getId());

        bookingSortDescByStart = (b1,b2) -> b2.getStart().compareTo(b1.getStart());

        bookingService = new BookingServiceImpl(bookingRepository,userRepository,itemRepository);

        userList = Data.generationData(2,User.class);
        Data.printList(userList,">>>");

        itemList = Data.generationData(2,Item.class,userList.get(0),1L);
        Data.printList(itemList,"===");

        bookingList = Data.generationData(8,Booking.class,userList.get(0),itemList.get(0));
        bookingList.sort(bookingSortDescById);
        Data.printList(bookingList,"_+_");
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
    void addBookingError2() {
        assertThrows(NotFoundException.class,() -> bookingService.addBooking(
                        new CreateBooking(1L,LocalDateTime.now().plusMinutes(1),LocalDateTime.now().plusDays(1)),1L),
                "Не найдена, при бронировании вещь!");
    }

    @Test
    void addBookingError3() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemList.get(0)));

        assertThrows(NotFoundException.class,() -> bookingService.addBooking(
                        new CreateBooking(1L,LocalDateTime.now().plusMinutes(1),LocalDateTime.now().plusDays(1)),1L),
                "Не может владелец вещи создать бронь на свою вещь!");

       /* assertThrows(BadRequestException.class,() -> bookingService.addBooking(
                        new CreateBooking(1L,LocalDateTime.now().minusDays(1),LocalDateTime.now().plusDays(1)),1L),
                "Время начала 2024-03-26T08:08:26.178605600 бронирования не может быть в прошлом!");*/
    }

    @Test
    void addBookingError4() {
        Item item = itemList.get(0).toBuilder().available(false).owner(userList.get(0)).build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.addBooking(
                        new CreateBooking(1L,LocalDateTime.now().plusSeconds(1),LocalDateTime.now().plusDays(1)),2L),
                "Вещь 1 уже забронирована!");
    }

    @Test
    void addBookingError5() {
        Item item = itemList.get(1).toBuilder().available(true).owner(userList.get(1)).build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class,() -> bookingService.addBooking(
                        new CreateBooking(2L,LocalDateTime.now().plusMinutes(1),LocalDateTime.now().plusDays(1)),10L),
                "Не найден, при бронировании пользователь 10");
    }

    @Test
    void addBooking() {

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemList.get(0).toBuilder().available(true).build()));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userList.get(1)));

        when(bookingRepository.save(any(Booking.class))).thenReturn(bookingList.get(0));

        Booking booking = bookingService.addBooking(
                new CreateBooking(1L,LocalDateTime.now().plusMinutes(1),LocalDateTime.now().plusDays(1)),2L);
        assertNotNull(booking);

        verify(bookingRepository).save(any(Booking.class));

    }

    @Test
    void setStatus() {
        assertThrows(NotFoundException.class,() -> bookingService.setStatus(1L,1L,false),
                "Не найдено бронирование под id = 1");

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingList.get(0).toBuilder().status(Status.WAITING).build()));

        assertThrows(BadRequestException.class,() -> bookingService.setStatus(1L,1L,null),
                "Не определен статус одобрения вещи на бронирование!");

        assertThrows(NotFoundException.class,() -> bookingService.setStatus(1L,1L,true),
                "Бронирующий 1 может только отменить бронь!");

        Item itemTest = itemList.get(0).toBuilder()
                .owner(userList.get(1))//владелец 2 user
                .build();
        Booking bookingTest = bookingList.get(0).toBuilder()
                .item(itemTest)
                .status(Status.APPROVED)
                .build();
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingTest));

        assertThrows(BadRequestException.class,
                () -> bookingService.setStatus(1L,userList.get(1).getId(),true),
                "Бронирование вещи 1 уже подтверждено владельцем 2!");

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingTest.toBuilder().status(Status.WAITING).build()));

        when(bookingRepository.save(any()))
                .thenReturn(bookingTest);

        Booking booking = bookingService.setStatus(1L,userList.get(1).getId(),true);
        assertNotNull(booking);

        bookingService.setStatus(1L,userList.get(1).getId(),false);

        verify(bookingRepository,times(2)).save(any());

    }

    @Test
    void getBookingByIdForUserId() {
        assertThrows(NotFoundException.class, () -> bookingService.getBookingByIdForUserId(1L,1L),
                "Не найден пользователь 1 при запросе брони 1 вещи");

        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingByIdForUserId(1L,1L),
         "Не найдено бронирование под id = 1");

        Item item = itemList.get(0).toBuilder().owner(userList.get(1)).build();
        Booking bookingTest = bookingList.get(0).toBuilder()
                .item(item)
                .booker(userList.get(1))
                .build();

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(bookingTest));


        assertThrows(NotFoundException.class, () -> bookingService.getBookingByIdForUserId(1L,1L),
                "Не найден владелец 1 вещи или заказчик 1 в бронировании 8");

        when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(bookingList.get(1)));

        Booking booking = bookingService.getBookingByIdForUserId(1L,1L);
        assertNotNull(booking);

    }

    @Test
    void getBookingsOwnerState()  {

        long bookerId = 1;

        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsOwnerState(bookerId, Data.ALL,0,10));

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(bookingRepository.findByItemOwnerIdOrderByIdDesc(anyLong(),any())).thenReturn(bookingList);

        List<Booking> list = bookingService.getBookingsOwnerState(bookerId, Data.ALL,0,10);
        assertNotNull(list);


        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(),any())).thenReturn(bookingList);

        list = bookingService.getBookingsOwnerState(bookerId,Data.FUTURE,0,10);
        assertNotNull(list);


        List<Booking> listWaiting =  bookingList.stream()
                .map(booking -> booking.toBuilder().status(Status.WAITING).build())
                .collect(Collectors.toList());
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByIdDesc(bookerId,Status.WAITING, PageRequest.of(0,10)))
                .thenReturn(listWaiting);

        list = bookingService.getBookingsOwnerState(bookerId,Data.WAITING,0,10);
        assertNotNull(list);
        assertEquals(Status.WAITING,list.get(5).getStatus());

        List<Booking> listRejected =  bookingList.stream()
                .map(booking -> booking.toBuilder().status(Status.REJECTED).build())
                .collect(Collectors.toList());
        when(bookingRepository.findByItemOwnerIdAndStatusOrderByIdDesc(bookerId,Status.REJECTED, PageRequest.of(0,10)))
                .thenReturn(listRejected);

        list = bookingService.getBookingsOwnerState(bookerId,Data.REJECTED,0,10);
        assertNotNull(list);
        assertEquals(Status.REJECTED,list.get(5).getStatus());

        when(bookingRepository.findByBookingCurrentForOwner(anyLong(), any(), any()))
                .thenReturn(bookingList);
        list = bookingService.getBookingsOwnerState(bookerId,Data.CURRENT,0,10);
        assertNotNull(list);
        assertEquals(8,list.size());

        when(bookingRepository.findByItemOwnerIdAndEndBeforeOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(bookingList);
        list = bookingService.getBookingsOwnerState(bookerId,Data.PAST,0,10);
        assertNotNull(list);
        assertEquals(8,list.size());

        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingList);

        list = bookingService.getBookingsOwnerState(bookerId,"",0,10);
        assertEquals(8,list.size());


        verify(bookingRepository).findByItemOwnerIdOrderByIdDesc(anyLong(),any());
        verify(bookingRepository,times(2)).findByItemOwnerIdOrderByStartDesc(anyLong(),any());
        verify(bookingRepository,times(2)).findByItemOwnerIdAndStatusOrderByIdDesc(anyLong(),any(),any());
        verify(bookingRepository).findByBookingCurrentForOwner(anyLong(),any(),any());
        verify(bookingRepository).findByItemOwnerIdAndEndBeforeOrderByEndDesc(anyLong(),any(),any());

    }

    @Test
    void getBookingsForBooker() {
        long bookerId = 1;

        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsForBooker(bookerId, Data.ALL,0,10));

        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(bookingRepository.findByBookerIdOrderByIdDesc(anyLong(),any())).thenReturn(bookingList);

        List<Booking> list = bookingService.getBookingsForBooker(bookerId, Data.ALL,0,10);

        assertNotNull(list);
        assertEquals(8,list.size());
        assertEquals(8,list.get(0).getId());
        assertEquals(7,list.get(1).getId());

        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(),any())).thenReturn(bookingList);

        list = bookingService.getBookingsForBooker(bookerId,Data.FUTURE,0,10);
        list.sort(bookingSortDescByStart);
        assertNotNull(list);
        assertEquals(8,list.size());
        assertEquals(8,list.get(0).getId());
        assertEquals(7,list.get(1).getId());

        List<Booking> listWaiting =  bookingList.stream()
                .map(booking -> booking.toBuilder().status(Status.WAITING).build())
                .collect(Collectors.toList());
        when(bookingRepository.findByBookerIdAndStatusOrderByIdDesc(bookerId,Status.WAITING, PageRequest.of(0,10)))
                .thenReturn(listWaiting);

        list = bookingService.getBookingsForBooker(bookerId,Data.WAITING,0,10);
        list.sort(bookingSortDescById);
        assertNotNull(list);
        assertEquals(8,list.size());
        assertEquals(8,list.get(0).getId());
        assertEquals(7,list.get(1).getId());
        assertEquals(Status.WAITING,list.get(5).getStatus());

        List<Booking> listRejected =  bookingList.stream()
                .map(booking -> booking.toBuilder().status(Status.REJECTED).build())
                .collect(Collectors.toList());
        when(bookingRepository.findByBookerIdAndStatusOrderByIdDesc(bookerId,Status.REJECTED, PageRequest.of(0,10)))
                .thenReturn(listRejected);

        list = bookingService.getBookingsForBooker(bookerId,Data.REJECTED,0,10);
        list.sort(bookingSortDescById);
        assertNotNull(list);
        assertEquals(8,list.size());
        assertEquals(8,list.get(0).getId());
        assertEquals(7,list.get(1).getId());
        assertEquals(Status.REJECTED,list.get(5).getStatus());

        when(bookingRepository.findByBookingCurrentForBooker(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingList);
        list = bookingService.getBookingsForBooker(bookerId,Data.CURRENT,0,10);
        assertNotNull(list);
        assertEquals(8,list.size());

        when(bookingRepository.findByBookerIdAndEndBeforeOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(bookingList);
        list = bookingService.getBookingsForBooker(bookerId,Data.PAST,0,10);
        assertNotNull(list);
        assertEquals(8,list.size());

        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(bookingList);

        list = bookingService.getBookingsForBooker(bookerId,"",0,10);
        assertEquals(8,list.size());


        verify(bookingRepository).findByBookerIdOrderByIdDesc(anyLong(),any());
        verify(bookingRepository,times(2)).findByBookerIdOrderByStartDesc(anyLong(),any());
        verify(bookingRepository,times(2)).findByBookerIdAndStatusOrderByIdDesc(anyLong(),any(),any());
        verify(bookingRepository).findByBookingCurrentForBooker(anyLong(),any(),any());
        verify(bookingRepository).findByBookerIdAndEndBeforeOrderByEndDesc(anyLong(),any(),any());

        verify(bookingRepository,never()).findAll();
    }
}