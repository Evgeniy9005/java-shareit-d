package ru.practicum.shareit.request;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.data.Data;
import ru.practicum.shareit.request.dto.CreateItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.data.Data.printList;


@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();

    private LocalDateTime data = LocalDateTime.of(2024,1,1,1,1,1);

    private List<ItemRequestDto> requestDtoList;

    private ItemRequestDto itemRequestDto1;

    private ItemRequestDto itemRequestDto2;

    @BeforeEach
    void start() {
        requestDtoList = Data.<ItemRequest>generationData(2, ItemRequest.class).stream()
                .map(itemRequest -> itemRequestMapper.toItemRequestDto(itemRequest.toBuilder().created(data).build()))
                .collect(Collectors.toList());
        printList(requestDtoList,">>>");

        itemRequestDto1 = requestDtoList.get(0);
        itemRequestDto2 = requestDtoList.get(1);

    }

    @Test
    void addItemRequest() throws Exception {

        when(itemRequestService.addItemRequest(any(CreateItemRequest.class),anyLong())).thenReturn(itemRequestDto1);
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

        when(itemRequestService.getItemsRequester(anyLong()))
                .thenReturn(requestDtoList);

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

        when(itemRequestService.getItemsRequesterPagination(anyLong(),any(Integer.class),any(Integer.class)))
                .thenReturn(requestDtoList);

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

        when(itemRequestService.getItemRequestByIdForOtherUser(anyLong(),anyLong()))
                .thenReturn(itemRequestDto1);

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