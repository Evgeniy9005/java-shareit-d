package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.practicum.shareit.data.Data.*;

class CommentTest {


    private List<Item> itemList;

    private List<User> userList;

    private List<Comment> commentList;

    private Comment comment1;

    private Comment comment2;

    @BeforeEach
    void start() {

        userList = Data.generationData(2,User.class);
        Data.printList(userList,">>>");

        itemList = Data.generationData(2,Item.class,userList.get(0),1L);
        Data.printList(itemList,"===");

        commentList = Data.generationData(2,Comment.class, itemList.get(0),userList.get(1));
        Data.printList(commentList,"=*=");

        comment1 = commentList.get(0);

        comment2 = commentList.get(1);
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
    void getItem() {
        assertEquals(itemList.get(0),comment1.getItem());
    }

    @Test
    void getAuthor() {
        assertEquals(userList.get(1),comment1.getAuthor());
    }

    @Test
    void getCreated() {
        Comment comment = comment1.toBuilder().created(CREATED_DATE).build();
        assertEquals(CREATED_DATE,comment.getCreated());
    }

    @Test
    void testToString() {
        assertEquals("Comment(id=1, text=Text1," +
                " item=Item(id=1, name=item1, description=описание вещи 1, " +
                "available=true, owner=User(id=1, name=User1, email=user1@mail), " +
                "request=1), author=User(id=2, name=User2, email=user2@mail), " +
                "created=2024-01-01T01:01:01)",comment1.toString());
    }

    @Test
    void builder() {
        assertNotNull(Comment.builder().build());
    }

    @Test
    void toBuilder() {
        assertNotNull(comment1.toBuilder().build());
    }
}