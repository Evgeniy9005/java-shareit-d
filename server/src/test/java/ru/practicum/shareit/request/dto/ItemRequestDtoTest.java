package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTesterItemRequestDto;

    @Autowired
    private JacksonTester<CreateItemRequest> jacksonTesterCreateItemRequest;


    private List<User> userList;

    private List<Item> itemList;

    private ItemMapper itemMapper;


    LocalDateTime created;

    @BeforeEach
    void start() {
        itemMapper = new ItemMapperImpl();

        userList = Data.<User>generationData(1,User.class);
        itemList = Data.<Item>generationData(2,Item.class,userList.get(0),1L);

        created = LocalDateTime.of(2024,1,1,1,1,1);

    }

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1,
                "описание",
                created,
                1L,
                itemMapper.toItemDtoList(itemList));
        JsonContent<ItemRequestDto> result = jacksonTesterItemRequestDto.write(itemRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("описание");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
        assertThat(result).extractingJsonPathNumberValue("$.requester").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("item1");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("описание вещи 1");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.items[0].comments").asList().size().isEqualTo(0);
        assertThat(result).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo("item2");
        assertThat(result).extractingJsonPathStringValue("$.items[1].description").isEqualTo("описание вещи 2");
        assertThat(result).extractingJsonPathNumberValue("$.items[1].owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.items[1].comments").asList().size().isEqualTo(0);
    }


    @Test
    void testCreateItemRequest() throws Exception {
        CreateItemRequest createItemRequest = new CreateItemRequest(1,"описание");

        JsonContent<CreateItemRequest> result = jacksonTesterCreateItemRequest.write(createItemRequest);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("описание");
    }
}