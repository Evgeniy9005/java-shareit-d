package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.data.Data.CREATED_DATE;

class CommentDtoTest {

    private List<UserDto> userDtoList;

    private List<CommentDto> commentDtoList;

    private CommentDto comment1;

    private CommentDto comment2;

    @BeforeEach
    void start() {

        userDtoList = Data.generationData(2,UserDto.class);
        Data.printList(userDtoList,">>>");


        commentDtoList = Data.generationData(2,CommentDto.class, userDtoList.get(1));
        Data.printList(commentDtoList,"=*=");

        comment1 = commentDtoList.get(0);

        comment2 = commentDtoList.get(1);
    }

    @Test
    void testEquals() {
        assertEquals(comment1,comment1.toBuilder().build());
    }

    @Test
    void testHashCode() {
        assertEquals(comment1.hashCode(),comment1.toBuilder().build().hashCode());
    }

    @Test
    void getId() {
        assertEquals(1,comment1.getId());
    }

    @Test
    void getText() {
        assertEquals("Text1",comment1.getText());
    }

    @Test
    void getCreated() {
        CommentDto comment = comment1.toBuilder().created(CREATED_DATE).build();
        assertEquals(CREATED_DATE,comment.getCreated());
    }

    @Test
    void testToString() {
        assertEquals("CommentDto(id=1, text=Text1, authorName=User2, " +
                "created=2024-01-01T01:01:01)",comment1.toString());
    }

    @Test
    void builder() {
        assertNotNull(CommentDto.builder().build());
    }

    @Test
    void toBuilder() {
        assertNotNull(comment1.toBuilder().build());
    }

    @Test
    void getAuthorName() {
        assertEquals(comment1.getAuthorName(),userDtoList.get(1).getName());
    }
}