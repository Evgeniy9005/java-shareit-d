package ru.practicum.shareit.patch;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.booking.IndicatorBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class PatchTest {


    @Test
    void patchUserDto() {

        UserDto userDto = UserDto.builder().id(1L).name("user").email("email").build();
        UserDto userDtoPatchAllNull = UserDto.builder().build();

        UserDto userDtoPatchNoName = UserDto.builder().id(1L).email("newemail@mail").build();

        UserDto userDtoPatchNoEmail = UserDto.builder().id(2L).name("newUser").build();

        UserDto userDtoPatchNoId = UserDto.builder().id(null).name("newUser").email("newemail@mail").build();

        UserDto userDtoPatchAll = UserDto.builder().id(3L).name("newUser").email("newemail@mail").build();

        UserDto userDtoTest = Patch.patchUserDto(userDto,userDtoPatchAllNull);
        assertEquals(1,userDtoTest.getId());
        assertEquals("user",userDtoTest.getName());
        assertEquals("email",userDtoTest.getEmail());

        userDtoTest = Patch.patchUserDto(userDto,userDtoPatchNoName);
        assertEquals(1,userDtoTest.getId());
        assertEquals("user",userDtoTest.getName());
        assertEquals("newemail@mail",userDtoTest.getEmail());

        userDtoTest = Patch.patchUserDto(userDto,userDtoPatchNoEmail);
        assertEquals(2,userDtoTest.getId());
        assertEquals("newUser",userDtoTest.getName());
        assertEquals("email",userDtoTest.getEmail());

        userDtoTest = Patch.patchUserDto(userDto,userDtoPatchNoId);
        assertEquals(1,userDtoTest.getId());
        assertEquals("newUser",userDtoTest.getName());
        assertEquals("newemail@mail",userDtoTest.getEmail());

        userDtoTest = Patch.patchUserDto(userDto,userDtoPatchAll);
        assertEquals(3,userDtoTest.getId());
        assertEquals("newUser",userDtoTest.getName());
        assertEquals("newemail@mail",userDtoTest.getEmail());

    }

    @Test
    void patchItemDto() {
        IndicatorBooking indicatorBooking1 = new IndicatorBooking(1,1);
        IndicatorBooking indicatorBooking2 = new IndicatorBooking(2,2);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .requestId(2L)
                .available(true)
                .lastBooking(indicatorBooking1)
                .nextBooking(indicatorBooking2)
                .build();

        ItemDto itemDtoPatch = ItemDto.builder()
                .id(2L)
                .name("name_patch")
                .description("description_patch")
                .requestId(3L)
                .available(false)
                .lastBooking(new IndicatorBooking(3,3))
                .nextBooking(new IndicatorBooking(4,4))
                .build();

        ItemDto itemDtoPatchAllNull = ItemDto.builder().build();

        ItemDto itemDtoPatchIdNull = itemDtoPatch.toBuilder().id(null).build();


        ItemDto itemDtoPatchNameNull = itemDtoPatch.toBuilder().name(null).build();

        ItemDto itemDtoPatchDescriptionNull = itemDtoPatch.toBuilder().description(null).build();

        ItemDto itemDtoPatchRequestIdNull = itemDtoPatch.toBuilder().requestId(null).build();

        ItemDto itemDtoPatchAvailableNull = itemDtoPatch.toBuilder().available(null).build();

        ItemDto itemDtoPatchLastBookingNull = itemDtoPatch.toBuilder().lastBooking(null).build();

        ItemDto itemDtoPatchNextBookingNull = itemDtoPatch.toBuilder().nextBooking(null).build();

        ItemDto itemDtoTest = Patch.patchItemDto(itemDto,itemDtoPatchAllNull);
        assertEquals(1,itemDtoTest.getId());
        assertEquals("name",itemDtoTest.getName());
        assertEquals("description",itemDtoTest.getDescription());
        assertTrue(itemDtoTest.getAvailable());
        assertEquals(2L,itemDtoTest.getRequestId());
        assertEquals(indicatorBooking1.getBookerId(),itemDtoTest.getLastBooking().getBookerId());
        assertEquals(indicatorBooking2.getBookerId(),itemDtoTest.getNextBooking().getBookerId());

        itemDtoTest = Patch.patchItemDto(itemDto,itemDtoPatchIdNull);
        assertEquals(1,itemDtoTest.getId());
        assertEquals("name_patch",itemDtoTest.getName());
        assertEquals("description_patch",itemDtoTest.getDescription());
        assertFalse(itemDtoTest.getAvailable());
        assertEquals(3L,itemDtoTest.getRequestId());
        assertEquals(3,itemDtoTest.getLastBooking().getBookerId());
        assertEquals(4,itemDtoTest.getNextBooking().getBookerId());

        itemDtoTest = Patch.patchItemDto(itemDto,itemDtoPatchNameNull);
        assertEquals(2,itemDtoTest.getId());
        assertEquals("name",itemDtoTest.getName());
        assertEquals("description_patch",itemDtoTest.getDescription());
        assertFalse(itemDtoTest.getAvailable());
        assertEquals(3L,itemDtoTest.getRequestId());
        assertEquals(3,itemDtoTest.getLastBooking().getBookerId());
        assertEquals(4,itemDtoTest.getNextBooking().getBookerId());

        itemDtoTest = Patch.patchItemDto(itemDto,itemDtoPatchDescriptionNull);
        assertEquals(2,itemDtoTest.getId());
        assertEquals("name_patch",itemDtoTest.getName());
        assertEquals("description",itemDtoTest.getDescription());
        assertFalse(itemDtoTest.getAvailable());
        assertEquals(3L,itemDtoTest.getRequestId());
        assertEquals(3,itemDtoTest.getLastBooking().getBookerId());
        assertEquals(4,itemDtoTest.getNextBooking().getBookerId());

        itemDtoTest = Patch.patchItemDto(itemDto,itemDtoPatchRequestIdNull);
        assertEquals(2,itemDtoTest.getId());
        assertEquals("name_patch",itemDtoTest.getName());
        assertEquals("description_patch",itemDtoTest.getDescription());
        assertFalse(itemDtoTest.getAvailable());
        assertEquals(2L,itemDtoTest.getRequestId());
        assertEquals(3,itemDtoTest.getLastBooking().getBookerId());
        assertEquals(4,itemDtoTest.getNextBooking().getBookerId());

        itemDtoTest = Patch.patchItemDto(itemDto,itemDtoPatchAvailableNull);
        assertEquals(2,itemDtoTest.getId());
        assertEquals("name_patch",itemDtoTest.getName());
        assertEquals("description_patch",itemDtoTest.getDescription());
        assertTrue(itemDtoTest.getAvailable());
        assertEquals(3L,itemDtoTest.getRequestId());
        assertEquals(3,itemDtoTest.getLastBooking().getBookerId());
        assertEquals(4,itemDtoTest.getNextBooking().getBookerId());

        itemDtoTest = Patch.patchItemDto(itemDto,itemDtoPatchLastBookingNull);
        assertEquals(2,itemDtoTest.getId());
        assertEquals("name_patch",itemDtoTest.getName());
        assertEquals("description_patch",itemDtoTest.getDescription());
        assertFalse(itemDtoTest.getAvailable());
        assertEquals(3L,itemDtoTest.getRequestId());
        assertEquals(1,itemDtoTest.getLastBooking().getBookerId());
        assertEquals(4,itemDtoTest.getNextBooking().getBookerId());

        itemDtoTest = Patch.patchItemDto(itemDto,itemDtoPatchNextBookingNull);
        assertEquals(2,itemDtoTest.getId());
        assertEquals("name_patch",itemDtoTest.getName());
        assertEquals("description_patch",itemDtoTest.getDescription());
        assertFalse(itemDtoTest.getAvailable());
        assertEquals(3L,itemDtoTest.getRequestId());
        assertEquals(3,itemDtoTest.getLastBooking().getBookerId());
        assertEquals(2,itemDtoTest.getNextBooking().getBookerId());
    }
}