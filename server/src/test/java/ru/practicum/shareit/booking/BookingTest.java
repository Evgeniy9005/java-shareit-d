package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.data.Data.*;

class BookingTest {


    protected List<Booking> bookingList;

    protected List<Item> itemList;

    protected List<User> userList;

    Booking booking1;

    Booking booking2;

    @BeforeEach
    void start() {

        userList = Data.generationData(2,User.class);
        Data.printList(userList,">>>");

        itemList = Data.generationData(2,Item.class,userList.get(0),1L);
        Data.printList(itemList,"===");

        bookingList = Data.generationData(2,Booking.class,userList.get(0),itemList.get(0));
        Data.printList(bookingList,"_+_");

        booking1 = bookingList.get(0);

        booking2 = bookingList.get(1);

    }


    @Test
    void getId() {
        assertEquals(1,booking1.getId());
    }

    @Test
    void getStart() {
        Booking booking = booking1.toBuilder().start(START_DATE).build();
        assertEquals(START_DATE,booking.getStart());
    }

    @Test
    void getEnd() {
        Booking booking = booking1.toBuilder().end(END_DATE).build();
        assertEquals(END_DATE,booking.getEnd());
    }

    @Test
    void getItem() {
        assertEquals(itemList.get(0),booking1.getItem());
    }

    @Test
    void getBooker() {
        assertEquals(userList.get(0),booking1.getBooker());
    }

    @Test
    void getStatus() {
        assertEquals(Status.APPROVED,booking1.getStatus());
    }

    @Test
    void testToString() {
        Booking booking = booking1.toBuilder().start(START_DATE).end(END_DATE).build();

        assertEquals("Booking(id=1, start=2024-02-02T02:02, end=2024-03-03T03:03, item=Item(id=1, " +
                        "name=item1, description=описание вещи 1, available=true, owner=User(id=1, name=User1," +
                        " email=user1@mail), request=1), booker=User(id=1, name=User1, email=user1@mail), " +
                        "status=APPROVED)", booking.toString());

    }

    @Test
    void builder() {
        assertNotNull(Booking.builder().build());
    }

    @Test
    void toBuilder() {
        assertNotNull(booking1.toBuilder().build());
    }
}