package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.Data;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    protected List<User> userList;

    private User user1;

    private User user2;

    @BeforeEach
    void start() {
        userList = Data.generationData(2, User.class);
        Data.printList(userList, ">>>");
        user1 = userList.get(0);
        user2 = userList.get(1);
    }

    @Test
    void testEquals() {
        assertEquals(new User(),new User());
    }

    @Test
    void testHashCode() {
        assertEquals(new User().hashCode(),new User().hashCode());
    }

    @Test
    void getId() {
        assertEquals(1,user1.getId());
        assertEquals(2,user2.getId());
    }

    @Test
    void getName() {
        assertEquals("User1",user1.getName());
    }

    @Test
    void getEmail() {
        assertEquals("user1@mail",user1.getEmail());
    }

    @Test
    void setId() {
        user1.setId(99);
        assertEquals(99,user1.getId());
    }

    @Test
    void setName() {
        user1.setName("");
        assertEquals("",user1.getName());
    }

    @Test
    void setEmail() {
        user1.setEmail("");
        assertEquals("",user1.getEmail());
    }

    @Test
    void testToString() {
        assertEquals("User(id=1, name=User1, email=user1@mail)",user1.toString());
    }
}