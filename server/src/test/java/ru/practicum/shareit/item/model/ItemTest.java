package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.user.User;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class ItemTest {

    private List<Item> itemList;

    private List<User> userList;

    private Item item1;

    private Item item2;

    @BeforeEach
    void start() {

        userList = Data.generationData(2,User.class);
        Data.printList(userList,">>>");

        itemList = Data.generationData(2,Item.class,userList.get(0),1L);
        Data.printList(itemList,"===");

        item1 = itemList.get(0);

        item2 = itemList.get(1);

    }

    @Test
    void testEquals() {
        assertEquals(new Item(),new Item());
        assertEquals(item1,item1.toBuilder().build());
    }

    @Test
    void testHashCode() {
        assertEquals(new Item().hashCode(),new Item().hashCode());
        assertEquals(item1.hashCode(),item1.toBuilder().build().hashCode());
    }

    @Test
    void getId() {
        assertEquals(1,item1.getId());
    }

    @Test
    void getName() {
        assertEquals("item1",item1.getName());
    }

    @Test
    void getDescription() {
        assertEquals("описание вещи 1",item1.getDescription());
    }

    @Test
    void isAvailable() {
        assertTrue(item1.isAvailable());
    }

    @Test
    void getOwner() {
        assertEquals(userList.get(0),item1.getOwner());
    }

    @Test
    void getRequest() {
        assertEquals(1,item1.getRequest());
    }

    @Test
    void setId() {
        item1.setId(99);
        assertEquals(99,item1.getId());
    }

    @Test
    void setName() {
        item1.setName("name");
        assertEquals("name",item1.getName());
    }

    @Test
    void setDescription() {
        item1.setDescription("d");
        assertEquals("d",item1.getDescription());
    }

    @Test
    void setAvailable() {
        item1.setAvailable(false);
        assertFalse(item1.isAvailable());
    }

    @Test
    void setOwner() {
        item1.setOwner(userList.get(1));
        assertEquals(userList.get(1),item1.getOwner());
    }

    @Test
    void setRequest() {
        item1.setRequest(0L);
        assertEquals(0,item1.getRequest());
    }

    @Test
    void testToString() {
        assertEquals("Item(id=1, name=item1, " +
                "description=описание вещи 1, available=true, " +
                "owner=User(id=1, name=User1, email=user1@mail), request=1)",
                item1.toString());
    }

    @Test
    void builder() {
        assertNotNull(Item.builder().build());
    }

    @Test
    void toBuilder() {
        assertEquals(item1,item1.toBuilder().build());
    }
}