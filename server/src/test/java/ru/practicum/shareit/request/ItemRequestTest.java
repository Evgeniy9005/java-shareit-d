package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.data.Data.*;

class ItemRequestTest {

    private List<ItemRequest> itemRequestList;

    private ItemRequest itemRequest;

    @BeforeEach
    void start() {

        itemRequestList = generationData(1, ItemRequest.class);
        printList(itemRequestList);

        itemRequest = itemRequestList.get(0).toBuilder()
                .created(CREATED_DATE)
                .build();
    }


    @Test
    void getId() {
        assertEquals(1,itemRequest.getId());
    }

    @Test
    void getDescription() {
        assertEquals("Запрос на вещь 1",itemRequest.getDescription());
    }

    @Test
    void getCreated() {
        assertEquals(CREATED_DATE,itemRequest.getCreated());
    }

    @Test
    void getRequester() {
        assertEquals(1,itemRequest.getRequester());
    }

    @Test
    void testToString() {
        assertEquals("ItemRequest(id=1, description=Запрос на вещь 1, created=2024-01-01T01:01, requester=1)",
                itemRequest.toString());
    }

    @Test
    void builder() {
        assertNotNull(ItemRequest.builder().build());
    }

    @Test
    void toBuilder() {
        assertEquals("Запрос на вещь 1",itemRequest.toBuilder().build().getDescription());
    }
}