package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentDto;
import ru.practicum.shareit.item.dto.IndicatorBooking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.data.Data.generationData;
import static ru.practicum.shareit.data.Data.printList;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;


    private LocalDateTime data = LocalDateTime.of(2024,1,1,1,1,1);

    private List<ItemDto> itemDtoList;

    private List<CommentDto> commentDtoList;

    private ItemDto itemDto1;

    private ItemDto itemDto2;

    @BeforeEach
    void start() {
        List<UserDto> userDtoList = generationData(2, UserDto.class);
        printList(userDtoList);

        itemDtoList = generationData(2, ItemDto.class, userDtoList.get(0),1L);
        printList(itemDtoList,">>>");

        commentDtoList = generationData(2,CommentDto.class, userDtoList.get(0));
        printList(commentDtoList,"_c_");

        itemDto1 = itemDtoList.get(0).toBuilder()
                .lastBooking(new IndicatorBooking(1,1))
                .nextBooking(new IndicatorBooking(2,2))
                .build();

        itemDto2 = itemDtoList.get(1).toBuilder()
                .lastBooking(new IndicatorBooking(2,2))
                .build();

    }

    @Test
    void addItem() throws Exception {
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(itemDto1, HttpStatus.OK);
        when(itemClient.addItem(any(ItemDto.class),anyLong())).thenReturn(objectResponseEntity);

        mvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.name",is(itemDto1.getName())))
                .andExpect(jsonPath("$.description",is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available",is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.requestId",is(1)))
                .andExpect(jsonPath("$.lastBooking.id",is(1)))
                .andExpect(jsonPath("$.lastBooking.bookerId",is(1)))
                .andExpect(jsonPath("$.nextBooking.id",is(2)))
                .andExpect(jsonPath("$.nextBooking.bookerId",is(2)));

    }

    @Test
    void upItem() throws Exception {
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(itemDto1, HttpStatus.OK);
        when(itemClient.upItem(any(ItemDto.class),anyLong(),anyLong())).thenReturn(objectResponseEntity);

        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.name",is(itemDto1.getName())))
                .andExpect(jsonPath("$.description",is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available",is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.requestId",is(1)))
                .andExpect(jsonPath("$.lastBooking.id",is(1)))
                .andExpect(jsonPath("$.lastBooking.bookerId",is(1)))
                .andExpect(jsonPath("$.nextBooking.id",is(2)))
                .andExpect(jsonPath("$.nextBooking.bookerId",is(2)));
    }

    @Test
    void getItemByRequestUsers() throws Exception  {
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(itemDto1, HttpStatus.OK);
        when(itemClient.getItemByRequestUsers(anyLong(),anyLong())).thenReturn(objectResponseEntity);

        mvc.perform(get("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.name",is(itemDto1.getName())))
                .andExpect(jsonPath("$.description",is(itemDto1.getDescription())))
                .andExpect(jsonPath("$.available",is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$.requestId",is(1)))
                .andExpect(jsonPath("$.lastBooking.id",is(1)))
                .andExpect(jsonPath("$.lastBooking.bookerId",is(1)))
                .andExpect(jsonPath("$.nextBooking.id",is(2)))
                .andExpect(jsonPath("$.nextBooking.bookerId",is(2)))
                .andExpect(jsonPath("$.comments",is(itemDto1.getComments())));
    }

    @Test
    void getItemsByUserId() throws Exception {
        List<ItemDto> dtoList = List.of(itemDto1,itemDto2);
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(dtoList, HttpStatus.OK);
        when(itemClient.getItemsByUserId(anyLong(),any(Integer.class),any(Integer.class)))
                .thenReturn(objectResponseEntity);

        mvc.perform(get("/items")
                        .content(objectMapper.writeValueAsString(itemDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",is(1),Integer.class))
                .andExpect(jsonPath("$[0].name",is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description",is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available",is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$[0].requestId",is(1)))
                .andExpect(jsonPath("$[0].lastBooking.id").value(1))
                .andExpect(jsonPath("$[0].lastBooking.bookerId",is(1)))
                .andExpect(jsonPath("$[0].nextBooking.id",is(2)))
                .andExpect(jsonPath("$[0].nextBooking.bookerId",is(2)))
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[1].id",is(2),Integer.class))
                .andExpect(jsonPath("$[1].name",is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description",is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available",is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].requestId",is(1)));

    }

    @Test
    void search() throws Exception {
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(itemDtoList, HttpStatus.OK);
        when(itemClient.search(anyString(),anyLong(),any(Integer.class),any(Integer.class)))
                .thenReturn(objectResponseEntity);

        mvc.perform(get("/items/search")
                        .content(objectMapper.writeValueAsString(itemDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text","value text")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",is(1),Integer.class))
                .andExpect(jsonPath("$[0].name",is(itemDto1.getName())))
                .andExpect(jsonPath("$[0].description",is(itemDto1.getDescription())))
                .andExpect(jsonPath("$[0].available",is(itemDto1.getAvailable())))
                .andExpect(jsonPath("$[0].requestId",is(1)))
                .andExpect(jsonPath("$[0].lastBooking").isEmpty())
                .andExpect(jsonPath("$[0].nextBooking").isEmpty())
                .andExpect(jsonPath("$[0].comments").isArray())
                .andExpect(jsonPath("$[1].id",is(2),Integer.class))
                .andExpect(jsonPath("$[1].name",is(itemDto2.getName())))
                .andExpect(jsonPath("$[1].description",is(itemDto2.getDescription())))
                .andExpect(jsonPath("$[1].available",is(itemDto2.getAvailable())))
                .andExpect(jsonPath("$[1].requestId",is(1)))
                .andExpect(jsonPath("$[1].lastBooking").isEmpty())
                .andExpect(jsonPath("$[1].nextBooking").isEmpty())
                .andExpect(jsonPath("$[1].comments").isArray());

    }

    @Test
    void addComment() throws Exception {

        CommentDto commentDto = commentDtoList.get(0);

        mvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(new CreateCommentDto(1L,"")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(commentDto, HttpStatus.OK);
        when(itemClient.addComment(any(CreateCommentDto.class),anyLong(),anyLong()))
                .thenReturn(objectResponseEntity);

        mvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.text",is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName",is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created",is(commentDto.getCreated().toString())));

    }
}