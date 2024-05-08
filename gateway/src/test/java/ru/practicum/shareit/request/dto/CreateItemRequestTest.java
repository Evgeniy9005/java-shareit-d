package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateItemRequestTest {
    private CreateItemRequest createItemRequest = new CreateItemRequest(1,"text");

    @Test
    void getId() {
        assertEquals(1,createItemRequest.getId());
    }

    @Test
    void getDescription() {
        assertEquals("text",createItemRequest.getDescription());
    }
}