package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateCommentDtoTest {
    private CreateCommentDto createCommentDto = new CreateCommentDto(1L,"text");

    @Test
    void getCommentId() {
        assertEquals(1,createCommentDto.getCommentId());
    }

    @Test
    void getText() {
        assertEquals("text",createCommentDto.getText());
    }

    @Test
    void testToString() {
        assertEquals("CreateCommentDto(commentId=1, text=text)",createCommentDto.toString());
    }
}