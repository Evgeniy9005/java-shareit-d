package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTesterItemDto;

    @Autowired
    private JacksonTester<CreateCommentDto> jacksonTesterCreateCommentDto;

    @Autowired
    private JacksonTester<CommentDto> jacksonTesterCommentDto;


    private List<UserDto> userDtoList;

    private List<ItemDto> itemDtoList;


    private List<CommentDto> commentDtoList;

    LocalDateTime created;

    private ItemDto itemDto;

    private ItemDto itemDto1;

    @BeforeEach
    void start() {

        userDtoList = Data.<UserDto>generationData(2,UserDto.class);
        itemDtoList = Data.<ItemDto>generationData(2,ItemDto.class, userDtoList.get(0),1L);
        commentDtoList = Data.<CommentDto>generationData(3,CommentDto.class, userDtoList.get(0));

        created = LocalDateTime.of(2024,1,1,1,1,1);

        itemDto = itemDtoList.get(0).toBuilder()
                .lastBooking(new IndicatorBooking(1,1))
                .nextBooking(new IndicatorBooking(2,2))
                .build();;

        itemDto1 = itemDtoList.get(1).toBuilder()
                .lastBooking(new IndicatorBooking(3,3))
                .nextBooking(new IndicatorBooking(4,4))
                .comments(commentDtoList)
                .build();;

    }

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = itemDtoList.get(0);

        JsonContent<ItemDto> result = jacksonTesterItemDto.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item1");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("описание вещи 1");
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathValue("$.comments").asList().size().isEqualTo(0);
        assertThat(result).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.owner.name").isEqualTo("User1");
        assertThat(result).extractingJsonPathStringValue("$.owner.email").isEqualTo("user1@mail");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);

    }

    @Test
    void testCommentDto() throws Exception {
        CommentDto commentDto = commentDtoList.get(0).toBuilder().created(created).build();

        JsonContent<CommentDto> result = jacksonTesterCommentDto.write(commentDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Text1");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("User1");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
    }

    @Test
    void testCreteCommentDtoItemDto() throws Exception {
        CreateCommentDto createCommentDto =  new CreateCommentDto(1L,"коммент");

        JsonContent<CreateCommentDto> result = jacksonTesterCreateCommentDto.write(createCommentDto);

        assertThat(result).extractingJsonPathNumberValue("$.commentId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("коммент");
    }


    @Test
    void testEquals() {
        assertEquals(ItemDto.builder().build(),ItemDto.builder().build());
        assertEquals(itemDto, itemDto.toBuilder().build());
    }

    @Test
    void testHashCode() {
        assertEquals(ItemDto.builder().build().hashCode(),ItemDto.builder().build().hashCode());
        assertEquals(itemDto.hashCode(), itemDto.toBuilder().build().hashCode());
    }

    @Test
    void getId() {
        assertEquals(1, itemDto.getId());
    }

    @Test
    void getName() {
        assertEquals("item1", itemDto.getName());
    }

    @Test
    void getDescription() {
        assertEquals("описание вещи 1", itemDto.getDescription());
    }

    @Test
    void getOwner() {
        assertEquals(userDtoList.get(0), itemDto.getOwner());
    }

    @Test
    void getRequest() {
        assertEquals(1, itemDto.getRequestId());
    }


    @Test
    void testToString() {
        assertEquals("ItemDto(id=1, name=item1, description=описание вещи 1, available=true, " +
                        "owner=UserDto(id=1, name=User1, email=user1@mail), requestId=1, " +
                        "lastBooking=IndicatorBooking(id=1, bookerId=1), " +
                        "nextBooking=IndicatorBooking(id=2, bookerId=2), comments=[])",
                itemDto.toString());
    }

    @Test
    void builder() {
        assertNotNull(ItemDto.builder().build());
    }

    @Test
    void toBuilder() {
        assertEquals(itemDto, itemDto.toBuilder().build());
    }

    @Test
    void getAvailable() {
        assertTrue(itemDto.getAvailable());
    }

    @Test
    void getLastBooking() {
        assertEquals(1,itemDto.getLastBooking().getBookerId());
        assertEquals(1,itemDto.getLastBooking().getId());
    }

    @Test
    void getNextBooking() {
        assertEquals(2,itemDto.getNextBooking().getBookerId());
        assertEquals(2,itemDto.getNextBooking().getId());
    }

    @Test
    void getComments() {
        assertEquals(List.of(),itemDto.getComments());
        assertEquals(commentDtoList,itemDto1.getComments());
    }

}