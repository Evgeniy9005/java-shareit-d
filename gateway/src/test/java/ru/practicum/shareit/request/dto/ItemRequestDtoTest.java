package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.data.Data.*;
import static ru.practicum.shareit.data.Data.CREATED_DATE;

class ItemRequestDtoTest {

    private List<ItemRequestDto> itemRequestList;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void start() {

        itemRequestList = generationData(1, ItemRequestDto.class);
        printList(itemRequestList);

        itemRequestDto = itemRequestList.get(0).toBuilder()
                .created(CREATED_DATE)
                .build();
    }


    @Test
    void getId() {
        assertEquals(1, itemRequestDto.getId());
    }

    @Test
    void getDescription() {
        assertEquals("Запрос на вещь 1", itemRequestDto.getDescription());
    }

    @Test
    void getCreated() {
        assertEquals(CREATED_DATE, itemRequestDto.getCreated());
    }

    @Test
    void getRequester() {
        assertEquals(1, itemRequestDto.getRequester());
    }

    @Test
    void testToString() {
        assertEquals("ItemRequestDto(id=1, description=Запрос на вещь 1," +
                        " created=2024-01-01T01:01, requester=1, items=[])",
                itemRequestDto.toString());
    }

    @Test
    void builder() {
        assertNotNull(ItemRequestDto.builder().build());
    }

    @Test
    void toBuilder() {
        assertEquals("Запрос на вещь 1", itemRequestDto.toBuilder().build().getDescription());
    }

    @Test
    void testEquals() {
    }

    @Test
    void canEqual() {
    }

    @Test
    void testHashCode() {
    }


    @Test
    void getItems() {
    }

}