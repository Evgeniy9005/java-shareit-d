package ru.practicum.shareit.request;

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
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.data.Data.printList;

@WebMvcTest(controllers = RequestItemController.class)
class RequestItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RequestItemClient client;

    @Autowired
    private MockMvc mvc;

    private LocalDateTime data = LocalDateTime.of(2024,1,1,1,1,1);

    private List<ItemRequestDto> requestDtoList;

    private ItemRequestDto itemRequestDto1;

    private ItemRequestDto itemRequestDto2;

    @BeforeEach
    void start() {
        requestDtoList = Data.<ItemRequestDto>generationData(2, ItemRequestDto.class).stream()
                .map(itemRequest -> itemRequest.toBuilder().created(data).build())
                .collect(Collectors.toList());
        printList(requestDtoList,">>>");

        itemRequestDto1 = requestDtoList.get(0);
        itemRequestDto2 = requestDtoList.get(1);

    }

    @Test
    void addItemRequest() throws Exception {

        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(new CreateItemRequest(0,"")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(itemRequestDto1, HttpStatus.OK);
        when(client.addItemRequest(any(CreateItemRequest.class),anyLong())).thenReturn(objectResponseEntity);
        mvc.perform(post("/requests")
                        .content(objectMapper.writeValueAsString(itemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.description",is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.created",is(itemRequestDto1.getCreated().toString())))
                .andExpect(jsonPath("$.requester",is(1)))
                .andExpect(jsonPath("$.items",is(itemRequestDto1.getItems())));

    }

    @Test
    void getItemsRequester() throws Exception {
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(requestDtoList, HttpStatus.OK);
        when(client.getItemsRequester(anyLong()))
                .thenReturn(objectResponseEntity);

        mvc.perform(get("/requests")
                        .content(objectMapper.writeValueAsString(requestDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",is(1),Integer.class))
                .andExpect(jsonPath("$[0].description",is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[0].created",is(itemRequestDto1.getCreated().toString())))
                .andExpect(jsonPath("$[0].requester",is(1)))
                .andExpect(jsonPath("$[0].items",is(itemRequestDto1.getItems())))
                .andExpect(jsonPath("$[1].id",is(2),Integer.class))
                .andExpect(jsonPath("$[1].description",is(itemRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].created",is(itemRequestDto2.getCreated().toString())))
                .andExpect(jsonPath("$[1].requester",is(1)))
                .andExpect(jsonPath("$[1].items",is(itemRequestDto2.getItems())));

    }

    @Test
    void getItemsRequesterPagination() throws Exception {
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(requestDtoList, HttpStatus.OK);
        when(client.getItemsRequesterPagination(anyLong(),any(Integer.class),any(Integer.class)))
                .thenReturn(objectResponseEntity);

        mvc.perform(get("/requests/all")
                        .content(objectMapper.writeValueAsString(requestDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1)
                        .param("from","0")
                        .param("size","10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id",is(1),Integer.class))
                .andExpect(jsonPath("$[0].description",is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[0].created",is(itemRequestDto1.getCreated().toString())))
                .andExpect(jsonPath("$[0].requester",is(1)))
                .andExpect(jsonPath("$[0].items",is(itemRequestDto1.getItems())))
                .andExpect(jsonPath("$[1].id",is(2),Integer.class))
                .andExpect(jsonPath("$[1].description",is(itemRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].created",is(itemRequestDto2.getCreated().toString())))
                .andExpect(jsonPath("$[1].requester",is(1)))
                .andExpect(jsonPath("$[1].items",is(itemRequestDto2.getItems())));


    }

    @Test
    void getItemRequestByIdForOtherUser() throws Exception {
        ResponseEntity<Object> objectResponseEntity = new ResponseEntity<>(itemRequestDto1, HttpStatus.OK);
        when(client.getItemRequestByIdForOtherUser(anyLong(),anyLong()))
                .thenReturn(objectResponseEntity);

        mvc.perform(get("/requests/1")
                        .content(objectMapper.writeValueAsString(requestDtoList))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id",1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(1),Integer.class))
                .andExpect(jsonPath("$.description",is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$.created",is(itemRequestDto1.getCreated().toString())))
                .andExpect(jsonPath("$.requester",is(1)))
                .andExpect(jsonPath("$.items",is(itemRequestDto1.getItems())));

    }

}