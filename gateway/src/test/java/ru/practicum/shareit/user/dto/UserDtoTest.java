package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.Data;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoTest {
    protected List<UserDto> userDtoList;

    private UserDto userDto;

    private UserDto userDto1;

    @BeforeEach
    void start() {
        userDtoList = Data.generationData(2, UserDto.class);
        Data.printList(userDtoList, ">>>");
        userDto = userDtoList.get(0);
        userDto1 = userDtoList.get(1);
    }

    @Test
    void testEquals() {
        assertEquals(UserDto.builder().build(),UserDto.builder().build());
    }

    @Test
    void testHashCode() {
        assertEquals(userDto.hashCode(),userDto.hashCode());
    }

    @Test
    void getId() {
        assertEquals(1, userDto.getId());
        assertEquals(2, userDto1.getId());
    }

    @Test
    void getName() {
        assertEquals("User1", userDto.getName());
    }

    @Test
    void getEmail() {
        assertEquals("user1@mail", userDto.getEmail());
    }



    @Test
    void testToString() {
        assertEquals("UserDto(id=1, name=User1, email=user1@mail)", userDto.toString());
    }

    @Test
    void toBuilder() {
        assertEquals(userDto,userDto.toBuilder().build());
    }
}