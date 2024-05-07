package ru.practicum.shareit.item.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.booking.IndicatorBooking;

import static org.junit.jupiter.api.Assertions.*;

class IndicatorBookingTest {

    private IndicatorBooking indicatorBooking = new IndicatorBooking(1,1);

    @Test
    void getId() {
        assertEquals(1,indicatorBooking.getId());
    }

    @Test
    void getBookerId() {
        assertEquals(1,indicatorBooking.getBookerId());
    }

    @Test
    void testToString() {
        assertEquals("IndicatorBooking(id=1, bookerId=1)",indicatorBooking.toString());
    }
}