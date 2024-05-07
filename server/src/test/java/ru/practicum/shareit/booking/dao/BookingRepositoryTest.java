package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.util.Util;
import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.data.Data.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EntityManager entityManager;

    private List<User> savedUser;

    private List<Item> savedItems;

    private List<Booking> savedBooking1;
    private List<Booking> savedBooking2;




    @BeforeEach
    void start() {
        savedUser = Data.<User>generationData(2,User.class)
                .stream()
                .map(user -> userRepository.save(user))
                .collect(Collectors.toList());
        printList(savedUser,"***");

        savedItems = Data.<Item>generationData(2,Item.class,savedUser.get(0),1L)
                .stream()
                .map(item -> itemRepository.save(item))
                .collect(Collectors.toList());
        Data.<Item>generationData(3,Item.class,savedUser.get(1),1L)
                .stream()
                .map(item -> savedItems.add(itemRepository.save(item.toBuilder().id(0).build())))
                .collect(Collectors.toList());
        printList(savedItems,"^^^");

        savedBooking1 = Data.<Booking>generationData(8, Booking.class,savedUser.get(0),savedItems.get(1))
                .stream()
                .map(b -> bookingRepository.save(b))
                .collect(Collectors.toList());
        printList(savedBooking1,"===");

        savedBooking2 = Data.<Booking>generationData(3, Booking.class,savedUser.get(1),savedItems.get(2))
                .stream()
                .map(b -> bookingRepository.save(b.toBuilder().id(0).build()))
                .collect(Collectors.toList());
        printList(savedBooking2,"=+=");

    }

    @AfterEach
    void end() {
        entityManager.createNativeQuery("ALTER TABLE BOOKINGS ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE ITEMS ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1").executeUpdate();
    }

    @Test
    void findAllOrderByIdDesc1() {

        Sort sortByDate = Sort.by(Sort.Direction.DESC,"start");
        Pageable pageable = Util.createPageParam(0,10,sortByDate);

        List<Booking> bookingList = bookingRepository.findAll(pageable).getContent();
        assertEquals(10,bookingList.size());

        pageable = Util.createPageParam(4,2,sortByDate);
        bookingList = bookingRepository.findAll(pageable).getContent();
        assertEquals(2,bookingList.size());

    }

    @Test
    void findByItemOwnerIdOrderByStartDesc1() {

        List<Booking> bookingList = pagination(0,10);
        assertEquals(8,bookingList.size());
        bookingList = pagination(3,2); //должен вернуть четвертый элемент по счету
        assertEquals(1,bookingList.size());
        assertEquals(5,bookingList.get(0).getId());
        bookingList = pagination(4,2); //должен вернуть пятый элемент по счету
        assertEquals(2,bookingList.size());
        bookingList = pagination(4,2); //должен вернуть пятый элемент по счету
        assertEquals(2,bookingList.size());
        bookingList = pagination(0,1); //должен вернуть первый элемент по счету
        assertEquals(1,bookingList.size());
        bookingList = pagination(0,3); //должен вернуть первый 3 элемента по счету
        assertEquals(3,bookingList.size());
        bookingList = pagination(4,3); //должен вернуть 5 и 6 элемент по счету
        assertEquals(2,bookingList.size());
        assertEquals(4,bookingList.get(0).getId());
        assertEquals(3,bookingList.get(1).getId());
        bookingList = pagination(6,3); //должен вернуть 7 и 8 элемент по счету
        assertEquals(2,bookingList.size());
        bookingList = pagination(7,3); //должен вернуть 8 элемент по счету
        assertEquals(1,bookingList.size());
        bookingList = pagination(1,1); //должен вернуть 2 элемент по счету
        assertEquals(1,bookingList.size());
        assertEquals(7,bookingList.get(0).getId());
    }



    private List<Booking> pagination(int from, int size) {
        Pageable page = Util.createPageParam(from,size);
        int start = Util.start(from,size);

        if (start == 0) {
            return bookingRepository.findByItemOwnerIdOrderByStartDesc(1, page);

        } else {
        return bookingRepository.findByItemOwnerIdOrderByStartDesc(1,page).stream()
                    .skip(start)
                    .collect(Collectors.toList());
        }
    }

    @Test
    void findByItemIdAndItemOwnerIdAndStatusOrderByStartAsc() {

        List<Booking> bookingList = bookingRepository
                .findByItemIdAndItemOwnerIdAndStatusOrderByStartAsc(2,1, Status.APPROVED);
        assertEquals(8,bookingList.size());
        assertEquals(List.of(1L,2L,3L,4L,5L,6L,7L,8L),bookingsId(bookingList));
    }

    @Test
    void existsByItemIdAndBookerIdAndStatusAndEndBefore() {
       boolean b = bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(2,
                       1,
                       Status.APPROVED,
               savedBooking1.get(0).getEnd().plusDays(1));
       assertTrue(b);
    }

    @Test
    void findByBookerIdOrderByIdDesc() {
        Pageable pageable = PageRequest.of(0,3);
        List<Booking> bookingList = bookingRepository.findByBookerIdOrderByIdDesc(1,pageable);
        assertEquals(3,bookingList.size());
        assertEquals(List.of(8L,7L,6L),bookingsId(bookingList));
    }

    @Test
    void findByBookerIdOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0,10);
        List<Booking> bookingList = bookingRepository.findByBookerIdOrderByStartDesc(1,pageable);
        assertEquals(List.of(8L,7L,6L,5L,4L,3L,2L,1L),bookingsId(bookingList));
        List<Booking> bookingList1 = bookingRepository.findByBookerIdOrderByStartDesc(2,pageable);
        assertEquals(List.of(11L,10L,9L),bookingsId(bookingList1));

    }

    @Test
    void findByItemOwnerIdOrderByIdDesc() {
        Pageable pageable = PageRequest.of(0,10);
        List<Booking> bookingList = bookingRepository.findByItemOwnerIdOrderByIdDesc(1,pageable);
        assertEquals(List.of(8L,7L,6L,5L,4L,3L,2L,1L),bookingsId(bookingList));
    }

    @Test
    void findByItemOwnerIdOrderByStartDesc() {
        Pageable pageable = PageRequest.of(0,10);
        List<Booking> bookingList = bookingRepository.findByItemOwnerIdOrderByStartDesc(1,pageable);
        assertEquals(List.of(8L,7L,6L,5L,4L,3L,2L,1L),bookingsId(bookingList));
    }

    @Test
    void findByBookerIdAndStatusOrderByIdDesc() {
        Pageable pageable = PageRequest.of(0,10);
        List<Booking> bookingList = bookingRepository
                .findByBookerIdAndStatusOrderByIdDesc(1,Status.APPROVED,pageable);
        assertEquals(List.of(8L,7L,6L,5L,4L,3L,2L,1L),bookingsId(bookingList));
    }

    @Test
    void findByBookingCurrentForBooker() {
        Pageable pageable = PageRequest.of(0,10);
        List<Booking> bookingList = bookingRepository
                .findByBookingCurrentForBooker(1, LocalDateTime.now(),pageable);
        assertEquals(List.of(1L,2L,3L,4L,5L,6L,7L,8L),bookingsId(bookingList));
    }

    @Test
    void findByBookerIdAndEndBeforeOrderByEndDesc() {
        Pageable pageable = PageRequest.of(0,10);
        List<Booking> bookingList = bookingRepository
                .findByBookerIdAndEndBeforeOrderByEndDesc(1, LocalDateTime.now().plusDays(1),pageable);
        assertEquals(List.of(8L,7L,6L,5L,4L,3L,2L,1L),bookingsId(bookingList));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByIdDesc() {
        Pageable pageable = PageRequest.of(0,10);
        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdAndStatusOrderByIdDesc(1, Status.APPROVED, pageable);
        assertEquals(List.of(8L,7L,6L,5L,4L,3L,2L,1L),bookingsId(bookingList));
    }

    @Test
    void findByBookingCurrentForOwner() {
        Pageable pageable = PageRequest.of(0,10);
        List<Booking> bookingList = bookingRepository
                .findByBookingCurrentForOwner(1, LocalDateTime.now(), pageable);
        assertEquals(List.of(1L,2L,3L,4L,5L,6L,7L,8L),bookingsId(bookingList));
    }

    @Test
    void findByItemOwnerIdAndEndBeforeOrderByEndDesc() {
        Pageable pageable = PageRequest.of(0,10);
        List<Booking> bookingList = bookingRepository
                .findByItemOwnerIdAndEndBeforeOrderByEndDesc(1, LocalDateTime.now().plusDays(1), pageable);
        assertEquals(List.of(8L,7L,6L,5L,4L,3L,2L,1L),bookingsId(bookingList));
    }

    @Test
    void findByItemsIdBooking() {
      List<Booking> bookings1 = bookingRepository.findByItemsIdBooking(List.of(1L,2L,3L),Status.APPROVED);
      assertEquals(11,bookings1.size());
      List<Booking> bookings2 = bookingRepository.findByItemsIdBooking(List.of(2L),Status.APPROVED);
      assertEquals(8,bookings2.size());
      List<Booking> bookings3 = bookingRepository.findByItemsIdBooking(List.of(1L,3L),Status.APPROVED);
      assertEquals(3,bookings3.size());
    }

    private List<Long> bookingsId(List<Booking> bookingList) {
        return bookingList.stream().map(booking -> booking.getId()).collect(Collectors.toList());
    }
}