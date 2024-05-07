package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.CommentMapperImpl;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> jacksonTesterItemDto;

    @Autowired
    private JacksonTester<CreateCommentDto> jacksonTesterCreateCommentDto;

    @Autowired
    private JacksonTester<CommentDto> jacksonTesterCommentDto;


    private List<User> userList;

    private List<Item> itemList;

    private ItemMapper itemMapper;

    private CommentMapper commentMapper;

    private List<Comment> commentList;

    LocalDateTime created;

    @BeforeEach
    void start() {
        itemMapper = new ItemMapperImpl();
        commentMapper = new CommentMapperImpl();

        userList = Data.<User>generationData(1,User.class);
        itemList = Data.<Item>generationData(1,Item.class,userList.get(0),1L);
        commentList = Data.<Comment>generationData(3,Comment.class,itemList.get(0),userList.get(0));

        created = LocalDateTime.of(2024,1,1,1,1,1);

    }

    @Test
    void testItemDto() throws Exception {
        ItemDto itemDto = itemMapper.toItemDto(itemList.get(0));

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
        CommentDto commentDto = commentMapper.toCommentDto(commentList.get(0).toBuilder().created(created).build());

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

}